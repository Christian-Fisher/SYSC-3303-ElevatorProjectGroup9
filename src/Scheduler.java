import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Scheduler is being used as a communication channel from Floor thread to Elevator thread and back again (for now)
 *
 * @author Dhyan Pathak, Karanvir Chaudhary
 * @version 1.0
 */
public class Scheduler {
	
	private static LinkedList<RequestData> requests = new LinkedList<RequestData>(); //behaves as a queue for requests
	private LinkedList<RequestData> completedRequests = new LinkedList<RequestData>();
	private ArrayList<ArrayList<Integer>> requestQueues =  new ArrayList<ArrayList<Integer>>();
	schedulerStateMachine currentState = schedulerStateMachine.noRequests;
	
	public Scheduler(int numOfElevators) {
		for(int i=0;i<numOfElevators;i++) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			requestQueues.add(temp);
		}
	}
	/**
	 * Places a request by added RequestData to requests.
	 * Notifies available elevators to accept new request
	 * @param r custom data structure RequestData that contains all necessary information of request
	 */
	public synchronized void placeRequest(RequestData r) {
		System.out.println("Scheduler state: "+currentState.toString());

		requests.add(r);
		System.out.println(r.getTime().toString() + " -  Request made at floor #" + r.getCurrentFloor() + 
				" to go " + r.getDirection().toString() + " to floor # " + r.getRequestedFloor());
		notifyAll();
		//The floor subsystem calls the placeRequest function and gives the Scheduler all the requests. 
		if(currentState.equals(Scheduler.schedulerStateMachine.noRequests)) {
			currentState = currentState.nextState(); //changes state to uncompletedRequests
			System.out.println("Scheduler state: "+currentState.toString());
		}
	}
	
	/**
	 * Method invoked by Elevator subsystem to accept new request to fulfill
	 * @return RequestData - peek of the first RequestData in requests queue
	 * @throws ParseException
	 */
	public synchronized RequestData processRequest() {
		while(requests.peek()==null) {		//Checks for the case where the list is empty
			try {
				wait();						//Waits until the list is not empty
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		RequestData r = requests.peek();
		System.out.println("Request at " + r.getTime().toString() + " (" + r.getCurrentFloor() + " -> " + r.getRequestedFloor() + ") is being processed");
		//the elevators call the processRequest, and they get back one of the requests saved in the scheduler. 
		//The elevator then processes the request and makes a call to completedRequest when finished. 

		currentState = currentState.nextState(); //Changes current state from uncompletedRequets to requestAdded. 

		System.out.println("Scheduler state: "+currentState.toString());
		return r;
		
	}
	
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
	 * Validates the completion of a request by an elevator and pops that request off the queue
	 * @param completionTime - datetime when elevator completed request so as to compare its finished after request date
	 * @param currentFloor - int of elevator's current position, gets cross referenced with original request's destination floor
	 * @param visitedRequestedFloor - boolean whether or not elevator passed/opened at the floor it was called on for, checks if thats the request source floor
	 * @return RequestData - popped RequestData from queue
	 */
	
	public synchronized void completeRequest(int elevatorID, int currentFloor) {
		//remove currentFloor from elevatorID's queue
		requestQueues.get(elevatorID).remove(currentFloor);
		
		boolean isDestination = false;
		for(RequestData r: requests) {
			if(r.getRequestedFloor() == currentFloor) {
				isDestination = true;
				toFloor(r);
				requests.remove(r);
				//if there are still requests in the elevator queue, send the next one
				if(requestQueues.get(elevatorID).size() != 0) {
					int nextFloor = requestQueues.get(elevatorID).get(0);
					moveElevator(elevatorID, nextFloor);
				}
			}
			else {
				int nextFloor = requestQueues.get(elevatorID).get(0);
				moveElevator(elevatorID, nextFloor);
			}
		}
		currentState = currentState.nextState();
		System.out.println("Scheduler state: "+currentState.toString());
	}
	
	//Enums used to represent the 4 states of the scheduler state machine. 
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
			//Option #1 is, if there are no more requests, then the state of the scheduler should be noRequests. 
			//Option #2 is, if there are still some requests left, then we should change the state to uncompletedRequests. 

			public schedulerStateMachine nextState() {
				if(requests.size() > 0) {
					return uncompletedRequests;
				}
				return noRequests;
			}
			
		};
		
		public abstract schedulerStateMachine nextState();
	}
	
	public static void main(String[] args) {
		int num = 4;
		Scheduler scheduler = new Scheduler(4);	
		Thread floorThread = new Thread(new FloorSubsystem(scheduler));	
		Thread elevatorThread = new Thread(new Elevator(scheduler));
		
		//initialize floor thread and passing this scheduler
		elevatorThread.start();
		floorThread.start();
		
		//initialize elevator threads and passing with scheduler
		
		//start all threads
	}	
}
