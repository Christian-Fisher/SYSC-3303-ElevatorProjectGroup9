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
	};
	
	/**
	 * Method invoked by Elevator subsystem to accept new request to fulfill
	 * @return RequestData - peek of the first RequestData in requests queue
	 * @throws ParseException
	 */
	public synchronized RequestData processRequest() {
		while(requests.peek()==null) {
			try {
				wait();
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		RequestData r = requests.peek();
		System.out.println("Request at " + r.getTime().toString() + " (" + r.getCurrentFloor() + " -> " + r.getRequestedFloor() + ") is being processed");
		return r;
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
			
			notifyAll();
			return requests.pop();
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
