import java.util.Date;
/**
 * This class represents an Elevator that is to be part of an elevator control simulator.
 * @author Sonia Hassan-legault, Nicholas Porter
 * @version 2.0 2020-02-15
 *
 */
public class Elevator implements Runnable{
	
	private Scheduler scheduler;
	private int currentFloor;
	private RequestData requestData = null;
	// initialize elevator state to current floor with doors closed
	ElevatorStateMachine currState = ElevatorStateMachine.CurrFloorDoorsClosed;
	
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
	 * 
	 * @author Nicholas Porter
	 * State machine for the elvator using
	 * Java enums
	 * 7 states in total
	 *
	 */
	public enum ElevatorStateMachine {
		
		CurrFloorDoorsClosed {
			@Override
			public ElevatorStateMachine nextState() {
				return Moving1;
			}
			
			
		},
		
		Moving1 {
			@Override
			public ElevatorStateMachine nextState() {
				return ArriveReqFloor;
			}
			
			
		},
		
		ArriveReqFloor {
			@Override
			public ElevatorStateMachine nextState() {
				return ReqFloorDoorsOpened;
			}
			
			
		},
		
		ReqFloorDoorsOpened {
			@Override
			public ElevatorStateMachine nextState() {
				return Moving2;
			}
			

			
		},
		
		Moving2 {
			@Override
			public ElevatorStateMachine nextState() {
				return ArriveDestFloor;
			}
			

			
		},
		
		ArriveDestFloor {
			@Override
			public ElevatorStateMachine nextState() {
				return DestFloorDoorsOpened;
			}

			
		},
		
		DestFloorDoorsOpened {
			@Override
			public ElevatorStateMachine nextState() {
				return CurrFloorDoorsClosed;
			}
			

			
		};
		
		public abstract ElevatorStateMachine nextState();
	}
	
	/**
	 * Runs continuously once the thread is started until the program is terminated
	 * Calls the scheduler to see if there is work to be done, moves accordingly, and then sends 
	 * a message back to the scheduler that it has moved accordingly.
	 */
	@Override
	public void run() {
		while(true) {
			
			// Switch statement for elevator state
			switch (currState) {
			
				case CurrFloorDoorsClosed: {
					//Elevator calling the scheduler
					requestData = scheduler.processRequest();
					
					currState = currState.nextState();
					break;
					
				}
				
				case Moving1: {
					//Elevator moves to the floor of the request
					
					int diff = currentFloor - requestData.getCurrentFloor();
					if(diff>0) {
						move(currentFloor, Direction.DOWN, requestData.getCurrentFloor());
					}
					else if(diff < 0) {
						move(currentFloor, Direction.UP, requestData.getCurrentFloor());
					}
					else {
						move(currentFloor, Direction.IDLE, requestData.getCurrentFloor());
					}

					currState = currState.nextState();
					break;
					
				}
				
				case ArriveReqFloor: {
					currState = currState.nextState();
					break;
					
				}
				
				case ReqFloorDoorsOpened: {
					currState = currState.nextState();
					break;
					
				}
				
				case Moving2: {
					//Elevator moves in the desired direction to the destination floor

					if(requestData.getDirection() == Direction.UP) {
						move(currentFloor, Direction.UP, requestData.getRequestedFloor());
					}
					else if(requestData.getDirection() == Direction.DOWN) {
						move(currentFloor, Direction.DOWN, requestData.getRequestedFloor());

					}
					else {
						move(currentFloor, Direction.IDLE, requestData.getRequestedFloor());
					}
					currState = currState.nextState();
					break;
					
				}
				
				case ArriveDestFloor: {
					currState = currState.nextState();
					break;
					
				}
				
				case DestFloorDoorsOpened: {
					//Elevator sends the info back
					Date date = new Date(System.currentTimeMillis());
					scheduler.completeRequest(date, this.currentFloor, true);
					currState = currState.nextState();
					break;
					
				}
			}
			System.out.println("Elevator state: "+currState);
		}
		
	}
	
	/**
	 * Method moves the elevator either up or down to the desired floor 
	 * @param currentFloor - int representing the floor the elevator is currently one
	 * @param d - Direction the elevator must move to reach desired floor
	 * @param desiredFloor - int representing the desired floor that has been requested to go to
	 */
	private void move(int currentFloor, Direction d, int desiredFloor) {
		if(d == Direction.UP) {
			for(int i = currentFloor; i <desiredFloor; i++) {
				this.setCurrentFloor(i+1);
				//System.out.println("At floor: "+ getCurrentFloor());
			}
		}
		else if(d == Direction.DOWN) {
			for(int i = currentFloor; i > desiredFloor; i--) {
				this.setCurrentFloor(i-1);
				//System.out.println("At floor: "+ getCurrentFloor());
			}
		}
		else {
			this.currentFloor = desiredFloor;
			//System.out.println("At floor: "+ getCurrentFloor());
		}
	}
}
