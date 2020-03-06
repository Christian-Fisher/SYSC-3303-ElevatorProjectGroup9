import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Scheduler is being used as a communication channel from Floor thread to Elevator thread and back again (for now)
 *
 * @author Dhyan Pathak
 * @version 1.0
 */
public class Scheduler {
	private LinkedList<RequestData> requests = new LinkedList<RequestData>(); //behaves as a queue for requests
	private LinkedList<RequestData> completedRequests = new LinkedList<RequestData>();
	/**
	 * Places a request by added RequestData to requests.
	 * Notifies available elevators to accept new request
	 * @param r custom data structure RequestData that contains all necessary information of request
	 */
	public synchronized void placeRequest(RequestData r) {
		requests.add(r);
		System.out.println(r.getDelay() + " -  Request made at floor #" + r.getCurrentFloor() + 
				" to go " + r.getDirection().toString() + " to floor # " + r.getRequestedFloor());
		notifyAll();
	};
	
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
		System.out.println("Request at " + r.getDelay() + " (" + r.getCurrentFloor() + " -> " + r.getRequestedFloor() + ") is being processed");
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
	
	public synchronized RequestData completeRequest(int completionTime, int currentFloor, boolean visitedRequestedFloor) {
		// TODO next iteration, a new param will replace all of this. It will have a list of objects consisting of tuples of
		// datetime of floor visited and visited floor number. This is to ensure elevator has accomplished multiple requests in a certain direction
		// by looping through this list of tuples and cross referencing with the requests list and popping 
		
		//check if elevator reached correct floor as per requests, check if elevator completed after the request time
		//and check if elevator stopped at request's current floor
		if(currentFloor == requests.peek().getRequestedFloor() && requests.peek().getDelay() < completionTime && visitedRequestedFloor) {
			System.out.println("Elevator completed request at "+completionTime);
			RequestData completedRequest = requests.pop();
			completedRequests.add(completedRequest);
			notifyAll();
			return completedRequest;
		}
		return null;
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
