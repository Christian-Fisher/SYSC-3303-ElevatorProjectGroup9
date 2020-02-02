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
			int diff = currentFloor - requestData.getCurrentFloor();
			if(diff>0) {
				move(currentFloor, RequestData.Direction.DOWN, requestData.getCurrentFloor());
			}
			else if(diff < 0) {
				move(currentFloor, RequestData.Direction.UP, requestData.getCurrentFloor());
			}
			
			//Elevator moves in the desired direction to the requested floor
			if(requestData.getDirection() == RequestData.Direction.UP) {
				move(currentFloor, RequestData.Direction.UP, requestData.getRequestedFloor());
			}
			else if(requestData.getDirection() == RequestData.Direction.DOWN) {
				move(currentFloor, RequestData.Direction.DOWN, requestData.getRequestedFloor());
			}
			else {
				move(currentFloor, RequestData.Direction.IDLE, requestData.getRequestedFloor());
			}
			//Elevator sends the info back
			Date date = new Date(System.currentTimeMillis());
			scheduler.completeRequest(date, this.currentFloor, true);
		}
		
	}
	
	/**
	 * Method moves the elevator either up or down to the desired floor 
	 * @param currentFloor - int representing the floor the elevator is currently one
	 * @param d - Direction the elevator must move to reach desired floor
	 * @param desiredFloor - int representing the desired floor that has been requested to go to
	 */
	private void move(int currentFloor, RequestData.Direction d, int desiredFloor) {
		if(d == RequestData.Direction.UP) {
			for(int i = currentFloor; i <desiredFloor; i++) {
				this.setCurrentFloor(i+1);
			}
		}
		else if(d == RequestData.Direction.DOWN) {
			for(int i = currentFloor; i > desiredFloor; i--) {
				this.setCurrentFloor(i-1);
			}
		}
		else {
			this.currentFloor = desiredFloor;
		}
	}
}
