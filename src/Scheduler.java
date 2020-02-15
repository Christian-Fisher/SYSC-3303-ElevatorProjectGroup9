import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Scheduler is being used as a communication channel from Floor thread to Elevator thread and back again (for now)
 *
 * @author Dhyan Pathak, Karanvir Chaudhary
 * @version 1.0
 */
public class Scheduler {
	private LinkedList<RequestData> requests = new LinkedList<RequestData>(); //behaves as a queue for requests
	private LinkedList<RequestData> completedRequests = new LinkedList<RequestData>();
	private schedulerStateMachine currentState = schedulerStateMachine.noRequests;
	
	/**
	 * Places a request by added RequestData to requests.
	 * Notifies available elevators to accept new request
	 * @param r custom data structure RequestData that contains all necessary information of request
	 */
	public synchronized void placeRequest(RequestData r) {
		requests.add(r);
		System.out.println(r.getTime().toString() + " -  Request made at floor #" + r.getCurrentFloor() + 
				" to go " + r.getDirection().toString() + " to floor # " + r.getRequestedFloor());
		notifyAll();
		//The floor subsystem calls the placeRequest function and gives the Scheduler all the requests. 
		//currentState = schedulerStateMachine.uncompletedRequests;
		currentState = currentState.nextState(); //changes state to uncompletedRequests
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
		//currentState = schedulerStateMachine.requestAdded;
		currentState =currentState.nextState(); //Changes current state from uncompletedRequets to requestAdded. 
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
	
	public synchronized RequestData completeRequest(Date completionTime, int currentFloor, boolean visitedRequestedFloor) {
		// TODO next iteration, a new param will replace all of this. It will have a list of objects consisting of tuples of
		// datetime of floor visited and visited floor number. This is to ensure elevator has accomplished multiple requests in a certain direction
		// by looping through this list of tuples and cross referencing with the requests list and popping 
		
		//check if elevator reached correct floor as per requests, check if elevator completed after the request time
		//and check if elevator stopped at request's current floor
		if(currentFloor == requests.peek().getRequestedFloor() && requests.peek().getTime().compareTo(completionTime) < 0 && visitedRequestedFloor) {
			System.out.println("Elevator completed request at "+completionTime.toString());
			RequestData completedRequest = requests.pop();
			completedRequests.add(completedRequest);
			notifyAll();
			//Once the elevator has processed the request, it sends back info to the scheduler. The scheduler 
			// then removes the request from requests, and adds it to the list of completed Requests. 
			
			//currentState = schedulerStateMachine.completedRequest;
			currentState = currentState.nextState();
			//Once a request has been completed, we should change its state. 
			//Option #1 is, if there are no more requests, then the state of the scheduler should be noRequests. 
			//Option #2 is, if there are still some requests left, then we should change the state to uncompletedRequests. 
			if(requests.isEmpty()) {
				//currentState = schedulerStateMachine.noRequests;
				currentState = currentState.nextState();
			}
			else {
				currentState=schedulerStateMachine.uncompletedRequests;
			}
			return completedRequest;
		}
		return null;
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
				return requestAdded;
			}
			
		},
		
		requestAdded {
			@Override
			public schedulerStateMachine nextState() {
				return uncompletedRequests;
			}
			
		},
		
		completedRequest {
			@Override
			public schedulerStateMachine nextState() {
				return noRequests;
			}
			
		};
		
		public abstract schedulerStateMachine nextState();
	}
	
	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();	
		Thread floorThread = new Thread(new FloorSubsystem(scheduler));	
		Thread elevatorThread = new Thread(new Elevator(scheduler));
		//initialize floor thread and passing this scheduler
		elevatorThread.start();
		floorThread.start();
		
		//initialize elevator threads and passing with scheduler
		
		//start all threads
	}	
}
