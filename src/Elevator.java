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
	private String Error;
	private boolean doorsOpen = false;

	public boolean isDoorsOpen() {
		return doorsOpen;
	}

	public void setDoorsOpen(boolean doorsOpen) {
		this.doorsOpen = doorsOpen;
	}
	public String getError() {
		return Error;
	}

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
		if (!Error.equals("hard")) {
			if (this.getDirection() == Direction.UP) {
				this.currentFloor++;
			} else if (this.getDirection() == Direction.DOWN) {
				this.currentFloor--;
			}
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
	 * @author Nicholas Porter State machine for the elvator using Java enums 4
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
				return ArriveReqFloor;
			}

		},
		transientError{
			@Override
			public ElevatorStateMachine nextState() {
				return ArriveReqFloor;
			}
		},
		
		hardState{
			public ElevatorStateMachine nextState() {
				return hardState;
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
					} catch (InterruptedException e) {
						System.out.println(e.toString());
					}
					break;
				}

				case Moving: {
					// Elevator moves to the floor of the request

					int diff = this.getCurrentFloor() - this.getRequestedFloor();
					if (diff > 0) {
						this.setDirection(Direction.DOWN);
						try {
							this.move();
							System.out.println("Elevator #" + this.elID + " is moving. Current Floor:  " + this.currentFloor + " Destination: " + this.requestedFloor);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else if (diff < 0) {
						this.setDirection(Direction.UP);
						try {
							this.move();
							System.out.println("Elevator #" + this.elID + " is moving. Current Floor: " + this.currentFloor + " Destination: " +  this.requestedFloor);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						System.out.println("Elevator #"+  this.elID + " has reached the requested floor (Floor #:" + this.requestedFloor + ")");
						this.setDirection(Direction.IDLE);
						currState = currState.nextState();
						break;
					}

					break;
				}

				case hardState:{
					System.out.println("The elevator with id:" + this.elID + "has experienced a hard fault error.");
					try {
						Thread.sleep(Long.MAX_VALUE);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					currState = currState.nextState();
					break;
				}
				
				case transientError:{
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						System.out.println(e.toString());
					}
					this.setError("null");
					Error = "null";
					currState = currState.nextState();
					break;
				}
				
				
				case ArriveReqFloor: {
					try {
						this.setDoors(true);
						Thread.sleep(1100);
						if (!this.isDoorsOpen()) {
							currState=ElevatorStateMachine.transientError;
							System.out.println("Elevator " + this.getElID()+ " in transient fault state");
							break;
						}
					} catch (InterruptedException e) {
						System.out.println("Door open Timer interrupted. Who did that?");
					}

					currState = currState.nextState();
					break;

				}

				case ReqFloorDoorsOpened: {
					try {
						this.setDoors(false);
						Thread.sleep(1100);
						if (this.isDoorsOpen()) {
							currState=ElevatorStateMachine.transientError;
							System.out.println("Elevator " + this.getElID()+ " in transient fault state");
							break;
						}
					} catch (InterruptedException e) {
						System.out.println("Door close Timer interrupted. Who did that?");

					}
					currState = currState.nextState();
					completeMove(elID, currentFloor, Error);
					break;

				}
			}
			if (currState != ElevatorStateMachine.CurrFloorDoorsClosed) {
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
		int startFloor = this.getCurrentFloor(); // Saves the position of the elevator before attempting a move.
		this.updateCurrentFloor();
		Thread.sleep(3000);
		if (this.getCurrentFloor() == startFloor) { // If the elevator has not moved after 3 seconds, it is stuck and
													// therefore is hard faulting
			System.out.println("Elevator " + this.getElID()+ "in hard fault state");
			currState=ElevatorStateMachine.hardState;
		}

	}

	// completing move asynchronously so that it can return back to state machine
	private void completeMove(int elID, int currentFloor, String errorMessage) {
		new Thread() {
			@Override
			public void run() {
				udp.completeMove(elID, currentFloor, errorMessage);
			}
		}.start();
	}

	private void setDoors(boolean open) {
		if (!Error.equals("transient")) {
			this.setDoorsOpen(open);
		}

	}

	public void setError(String errorMessage) {
		this.Error = errorMessage;
		System.out.println("Elevator " + this.getElID() + " Error Status: " + errorMessage);

	}
}
