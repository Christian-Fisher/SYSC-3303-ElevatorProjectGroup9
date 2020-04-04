/**
 * This class represents an Elevator that is to be part of an elevator control
 * simulator.
 * 
 * @author Sonia Hassan-legault, Nicholas Porter
 * @version 3.0 2020-02-15
 *
 */
public class Elevator implements Runnable {
	private elevatorUDPThread udp;
	private int currentFloor, requestedFloor;
	private Direction dir;
	private int elID;
	private static String elevatorErrorHolder;
	public int getElID() {
		return elID;
	}

	// intialize elevator state to current floor with doors closed
	private ElevatorStateMachine currState;

	/**
	 * Constructor for the Elevator class
	 * 
	 * @param scheduler - A Scheduler to coordinate when work must be done
	 */
	public Elevator(int elID) {
		this.elID = elID;
		requestedFloor = 0;
		currentFloor = 0;
		dir = Direction.IDLE;
		currState = ElevatorStateMachine.CurrFloorDoorsClosed;
		udp = new elevatorUDPThread(elID, this);
		Thread udpThread = new Thread(udp);
		udpThread.setName("Elevator: " + elID);
		udpThread.start();
	}

	/**
	 * Returns the current floor this elevator is on
	 * 
	 * @return int - the current floor
	 */
	public int getCurrentFloor() {
		return this.currentFloor;
	}

	/**
	 * Update (increment / decrement) currentFloor based on direction elevator is
	 * travelling
	 */
	public void updateCurrentFloor() {
		if (this.getDirection() == Direction.UP) {
			this.currentFloor++;
		} else if (this.getDirection() == Direction.DOWN) {
			this.currentFloor--;
		}
	}

	public synchronized int getRequestedFloor() {
		return requestedFloor;
	}

	public synchronized void setRequestedFloor(int requestedFloor) {
		this.requestedFloor = requestedFloor;
	}

	/**
	 * 
	 * @return Direction elevator is travelling
	 */
	public Direction getDirection() {
		return this.dir;
	}

	/**
	 * Set the direction the elevator is travelling
	 * 
	 * @param d (Direction)
	 */
	public void setDirection(Direction d) {
		this.dir = d;
	}

	/**
	 * 
	 * @author Nicholas Porter, Karanvir Chaudhary State machine for the elvator using Java enums 4
	 *         states in total
	 *
	 */
	public enum ElevatorStateMachine {
		CurrFloorDoorsClosed {
			@Override
			public ElevatorStateMachine nextState() {
				return Moving;
			}
		},

		Moving {
			@Override
			public ElevatorStateMachine nextState() {
				if(elevatorErrorHolder == null) {
					return ArriveReqFloor;
				}
				else {
					return error;
				}
			}
		},
		error{
			@Override
			public ElevatorStateMachine nextState() {
				if(elevatorErrorHolder == "transient") {
					return transientError;
				}
				else {
					return null;
				}
			}
		},
		transientError{
			@Override
			public ElevatorStateMachine nextState() {
				return Moving;
			}
		}, 
		/*
		OutOfService{
			@Override
			public ElevatorStateMachine nextState() {
				return null;
			}
		},
		*/
		ArriveReqFloor {
			@Override
			public ElevatorStateMachine nextState() {
				return ReqFloorDoorsOpened;
			}
		},
		ReqFloorDoorsOpened {
			@Override
			public ElevatorStateMachine nextState() {
				return CurrFloorDoorsClosed;
			}
		};

		public abstract ElevatorStateMachine nextState();
	}
	/**
	 * Runs continuously once the thread is started until the program is terminated
	 * Calls the scheduler to see if there is work to be done, moves accordingly,
	 * and then sends a message back to the scheduler that it has moved accordingly.
	 */
	@Override
	public void run() {
		while (true) {
			// Switch statement for elevator state
			switch (currState) {
				case CurrFloorDoorsClosed: {
					// Elevator calling the scheduler
					if (this.getCurrentFloor() != this.getRequestedFloor()) {
						currState = currState.nextState();
						break;
					}
					try {
						Thread.sleep(250);
					}catch(InterruptedException e) {
						System.out.println(e.toString());
					}
					break;
				}
	
				case Moving: {
					// Elevator moves to the floor of the request
					if(elevatorErrorHolder == null) {
						int diff = this.getCurrentFloor() - this.getRequestedFloor();
						if (diff > 0) {
							this.setDirection(Direction.DOWN);
							try {
								this.move();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else if (diff < 0) {
							this.setDirection(Direction.UP);
							try {
								this.move();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							this.setDirection(Direction.IDLE);
							currState = currState.nextState();
							break;
						}
	
						break;
					}
					else {
						currState = currState.nextState();
						break;
					}
				}
				
				case error:{
					currState = currState.nextState();
					break;
				}
				case transientError: {
					for(int i =0;i<10000;i++) {}
					elevatorErrorHolder = null;
					this.setError(null);
					currState = currState.nextState();
					break;
				}
				
	
				case ArriveReqFloor: {
					currState = currState.nextState();
					break;
	
				}
				
				case ReqFloorDoorsOpened: {
					currState = currState.nextState();
					completeMove(elID, currentFloor);
					break;
	
				}
			}
			if (currState != ElevatorStateMachine.CurrFloorDoorsClosed) {
				System.out.println("Elevator "+elID+": Current state: " + currState);
			}
		}
	}

	/**
	 * Method moves the elevator either up or down to the desired floor
	 * 
	 * @param currentFloor - int representing the floor the elevator is currently
	 *                     one
	 * @param d            - Direction the elevator must move to reach desired floor
	 * @param desiredFloor - int representing the desired floor that has been
	 *                     requested to go to
	 * @throws InterruptedException
	 */
	private void move() throws InterruptedException {

		this.updateCurrentFloor();
		System.out.println("Elevator "+elID+": At floor: " + getCurrentFloor());
		Thread.sleep(2000);

	}
	
	//completing move asynchronously so that it can return back to state machine
	private void completeMove(int elID, int currentFloor) {
		new Thread() {
		   @Override
		   public void run() {
				udp.completeMove(elID, currentFloor);
		   }
		}.start();
	}

	public void setError(String errorMessage) {
		System.out.println("Elevator "+ this.getElID() + " Error Status: " + errorMessage);
		elevatorErrorHolder = errorMessage;
		
	}
	public String getError() {
		return elevatorErrorHolder;
	}
}
