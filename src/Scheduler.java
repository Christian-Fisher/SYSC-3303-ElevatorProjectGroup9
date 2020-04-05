import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class ElevatorDetail implements Comparable<ElevatorDetail> {
	int id;
	Direction direction;
	Direction desiredDirection;
	int distance;

	public ElevatorDetail(int id, Direction direction, Direction desiredDirection, int distance) {
		this.id = id;
		this.direction = direction;
		this.desiredDirection = desiredDirection;
		this.distance = distance;
	}

	public int compareTo(ElevatorDetail otherE) {
        if(this.distance == 0 && otherE.distance > 0) {
			return 1; 
		}
		if(otherE.distance == 0 && this.distance > 0) {
			return -1; 
		}
		if(this.desiredDirection != this.direction && this.direction != Direction.IDLE && otherE.direction == Direction.IDLE) {
			return 1;
		}
		if(this.desiredDirection != otherE.direction && otherE.direction != Direction.IDLE && this.direction == Direction.IDLE) {
			return -1;
		}
		if((this.desiredDirection == this.direction && this.desiredDirection == otherE.direction) ||
				(this.desiredDirection != this.direction && this.desiredDirection != otherE.direction)) {
			if(this.distance == otherE.distance) {
				return 0;
			}
			return this.distance < otherE.distance ? -1 : 1;
		}
		if (this.desiredDirection != otherE.direction && this.desiredDirection == this.direction && otherE.direction != Direction.IDLE) {
			return -1;
		}
		if (this.desiredDirection != this.direction && this.desiredDirection == otherE.direction && this.direction != Direction.IDLE) {
			return 1;
		}
		if (this.distance < otherE.distance) {
			return -1;
		} else if (this.distance > otherE.distance) {
			return 1;
		} else {
			return 0;
		}
	}
}

/**
 * Scheduler is being used as a communication channel from Floor thread to
 * Elevator thread and back again (for now)
 *
 * @author Dhyan Pathak, Karanvir Chaudhary
 * @version 1.0
 */
public class Scheduler {
	private schedulerUDPThread udp = new schedulerUDPThread(this);
	private static LinkedList<RequestData> requests = new LinkedList<RequestData>(); // behaves as a queue for requests
	private LinkedList<RequestData> completedRequests = new LinkedList<RequestData>();
	schedulerStateMachine currentState = schedulerStateMachine.noRequests;

	// 2D Array representing queue for every elevator
	private ArrayList<ArrayList<Integer>> elevators = new ArrayList<ArrayList<Integer>>();

	public Scheduler(int numOfElevators) {
		Thread udpThread = new Thread(udp);
		udpThread.setName("udpThread");
		udpThread.start();
		for (int i = 0; i < numOfElevators; i++) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			elevators.add(temp);
		}
	}

	/**
	 * Places a request by added RequestData to requests. Notifies available
	 * elevators to accept new request
	 * 
	 * @param r custom data structure RequestData that contains all necessary
	 *          information of request
	 */
	public synchronized void placeRequest(RequestData r) {
		System.out.println("Scheduler state: " + currentState.toString());

		System.out.println(r.getDelay() + " -  Request made at floor #" + r.getCurrentFloor() + " to go "
				+ r.getDirection().toString() + " to floor # " + r.getRequestedFloor());

		// int for current and destination floor
		int c = r.getCurrentFloor();
		int d = r.getRequestedFloor();

		// find elevator in elevators list that is closest to int c
		// List where first element is best. Will be sorted based on request's direction
		// and floor
		List<ElevatorDetail> consideredElevators = new ArrayList<ElevatorDetail>();
		RequestData[] polledElevators = udp.pollElevators(); // get the in processing request data for every elevator
		
		for (int i = 0; i < polledElevators.length; i++) {
			if(!polledElevators[i].getErrorMessage().equals("null")) { // not considering elevators that have an error
				continue;
			}
			int distanceFromCurrentFloor = Math.abs(c - polledElevators[i].getCurrentFloor());

			ElevatorDetail ed = new ElevatorDetail(polledElevators[i].getElevatorID(),
					polledElevators[i].getDirection(), r.getDirection(), distanceFromCurrentFloor);
			System.out.println("Distance to elevator "+polledElevators[i].getElevatorID()+": "+distanceFromCurrentFloor 
					+ ", Direction: "+polledElevators[i].getDirection());
			consideredElevators.add(ed);
		}
		Collections.sort(consideredElevators); // sort the elevators
		int optimalElevatorID = consideredElevators.get(0).id;
		System.out.println("Optimal Elevator: "+optimalElevatorID);

		r.setElevatorID(optimalElevatorID); // set the elevator id to optimally selected elevator
		ArrayList<Integer> optimalElevator = elevators.get(optimalElevatorID); // get the elevator's queue

		optimalElevator.add(c); // add floors
		optimalElevator.add(d);
		
		if(consideredElevators.get(0).direction == Direction.DOWN || d - c < 0) {
			Collections.sort(optimalElevator, Collections.reverseOrder()); //sort the floors decreasing
		} else {
			Collections.sort(optimalElevator); // sort the floors increasing 
		}
		System.out.println("Add "+c+" & "+d+" to elevator "+optimalElevatorID+" Elevator queue: "+ optimalElevator);

		elevators.set(optimalElevatorID, optimalElevator);

		udp.moveElevator(optimalElevatorID, optimalElevator.get(0), r.getErrorMessage()); // tell chosen elevator to go to first request
																		// floor
		r.setErrorMessage(null);
		requests.add(r);
		// The floor subsystem calls the placeRequest function and gives the Scheduler
		// all the requests.
		if (currentState.equals(Scheduler.schedulerStateMachine.noRequests)) {
			currentState = currentState.nextState(); // changes state to uncompletedRequests
			System.out.println("Scheduler state: " + currentState.toString());
		}
	}

	/**
	 * Method invoked by Elevator subsystem to accept new request to fulfill
	 * 
	 * @return RequestData - peek of the first RequestData in requests queue
	 * @throws ParseException
	 */
//	public synchronized RequestData processRequest() {
//		while(requests.peek()==null) {		//Checks for the case where the list is empty
//			try {
//				wait();						//Waits until the list is not empty
//			}catch(InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		RequestData r = requests.peek();
//		System.out.println("Request at " + r.getTime().toString() + " (" + r.getCurrentFloor() + " -> " + r.getRequestedFloor() + ") is being processed");
//		//the elevators call the processRequest, and they get back one of the requests saved in the scheduler. 
//		//The elevator then processes the request and makes a call to completedRequest when finished. 
//
//		currentState = currentState.nextState(); //Changes current state from uncompletedRequets to requestAdded. 
//
//		System.out.println("Scheduler state: "+currentState.toString());
//		return r;
//		
//	}

	/**
	 * 
	 * @return boolean: is the completedRequests list empty
	 */
	public synchronized boolean isCompletedListEmpty() {
		return completedRequests.isEmpty();
	}

	/**
	 * 
	 * @return The last event in the completedRequests list
	 */
	public synchronized RequestData getCompletedRequest() {
		return completedRequests.pop();
	}

	/**
	 * Validates the completion of a request by an elevator and pops that request
	 * off the queue
	 * 
	 * @param completionTime        - datetime when elevator completed request so as
	 *                              to compare its finished after request date
	 * @param currentFloor          - int of elevator's current position, gets cross
	 *                              referenced with original request's destination
	 *                              floor
	 * @param visitedRequestedFloor - boolean whether or not elevator passed/opened
	 *                              at the floor it was called on for, checks if
	 *                              thats the request source floor
	 * @return RequestData - popped RequestData from queue
	 */

	public synchronized void completeRequest(int elevatorID, int currentFloor, String errorMessage) {
		// remove currentFloor from elevatorID's queue
		int indexOfCurrentFloor = elevators.get(elevatorID).indexOf(currentFloor);
		boolean lastRequest = false;
		if(elevators.get(elevatorID).size() == 1) { lastRequest = true; }
		elevators.get(elevatorID).remove(indexOfCurrentFloor);
		Iterator<RequestData> rIterator = requests.iterator();
		while(rIterator.hasNext()) {
			RequestData r = rIterator.next();
			// destination floor
			if (r.getElevatorID() == elevatorID ) {
					if(lastRequest || r.getRequestedFloor() == currentFloor) {
						udp.toFloor(r);
						rIterator.remove();
						currentState = currentState.nextState();
						// if there are still requests in the elevator queue, send the next one
						if (elevators.get(elevatorID).size() != 0) {
							int nextFloor = elevators.get(elevatorID).get(0);
							System.out.println("New elevator move id=" + elevatorID+ "to floor "+nextFloor);
							sendMove(elevatorID, nextFloor,errorMessage);
							break;
						}
					} else {
						int nextFloor = elevators.get(elevatorID).get(0);
						System.out.println("New elevator move id=" + elevatorID+ " to floor "+nextFloor);
						sendMove(elevatorID, nextFloor,errorMessage);
						break;
					}
			}
		}
		
		System.out.println("Scheduler state: " + currentState.toString());
		return;
	}
	
	//sending next move asynchronously so that the original function can return
	private void sendMove(int elevatorID, int nextFloor, String errorMessage) {
		new Thread() {
		   @Override
		   public void run() {
				udp.moveElevator(elevatorID, nextFloor, errorMessage);
		   }
		}.start();
	} 
	
	// Enums used to represent the 4 states of the scheduler state machine.
	public enum schedulerStateMachine {
		noRequests {
			@Override
			public schedulerStateMachine nextState() {
				return uncompletedRequests;
			}

		},

		uncompletedRequests {
			@Override
			public schedulerStateMachine nextState() {
				return completedRequest;
			}

		},

		completedRequest {
			@Override
			// Option #1 is, if there are no more requests, then the state of the scheduler
			// should be noRequests.
			// Option #2 is, if there are still some requests left, then we should change
			// the state to uncompletedRequests.

			public schedulerStateMachine nextState() {
				synchronized(requests) {
					if (requests.size() > 0) {
						return uncompletedRequests;
					}
					return noRequests;
				}
			}

		};

		public abstract schedulerStateMachine nextState();
	}

	public static void main(String[] args) {
		int numOfElevators = 4;
		Scheduler scheduler = new Scheduler(numOfElevators);
		Thread floorThread = new Thread(new FloorSubsystem());
		floorThread.setName("floorThread");
		Thread[] elevatorThreadArray = new Thread[numOfElevators];
		for (int i = 0; i < numOfElevators; i++) {
			elevatorThreadArray[i] = new Thread(new Elevator(i));
			elevatorThreadArray[i].setName("Elevator" + i);
			elevatorThreadArray[i].start();
		}
		// initialize floor thread and passing this scheduler
		
		floorThread.start();

		// initialize elevator threads and passing with scheduler

		// start all threads
	}
}
