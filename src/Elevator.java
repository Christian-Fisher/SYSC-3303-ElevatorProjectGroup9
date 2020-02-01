import java.util.Date;
/**
 * This class represents an Elevator that is to be part of an elevator control simulator.
 * @author SONIA
 * @version 1.0 2020-02-01
 *
 */
public class Elevator implements Runnable{
	
	private Scheduler scheduler;
	private int currentFloor;

	/**
	 * Constructor for the Elevator class
	 * @param scheduler - A Scheduler to coordinate when work must be done
	 */
	public Elevator(Scheduler scheduler) {
		this.scheduler = scheduler;
		currentFloor = 0;
	}

	/**
	 * Returns the current floor this elevator is on
	 * @return int - the current floor
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}
	
	/**
	 * Set the current floor number to the value of newFloor
	 * @param int - newFloor
	 */
	public void setCurrentFloor(int newFloor) {
		this.currentFloor = newFloor;
	}
	
	/**
	 * Runs continuously once the thread is started until the program is terminated
	 * Calls the scheduler to see if there is work to be done, moves accordingly, and then sends 
	 * a message back to the scheduler that it has moved accordingly.
	 */
	@Override
	public void run() {
		while(true) {
			//Elevator calling the scheduler
			RequestData requestData = scheduler.processRequest();
			currentFloor = requestData.getCurrentFloor();
			
			//Elevator moves in the desired direction to the requested floor
			if(requestData.getDirection() == RequestData.Direction.UP) {
				for(int i = currentFloor; i <requestData.getRequestedFloor(); i++) {
					this.setCurrentFloor(i+1);
				}
			}
			else if(requestData.getDirection() == RequestData.Direction.DOWN) {
				for(int i = currentFloor; i <requestData.getRequestedFloor(); i--) {
					this.setCurrentFloor(i-1);
				}
			}
			else {
				currentFloor = requestData.getRequestedFloor();
			}
			//Elevator sends the info back
			Date date = new Date(System.currentTimeMillis());
			scheduler.completeRequest(date, this.currentFloor, true);
		}
		
	}
}
