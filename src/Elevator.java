/**
 * This class represents an Elevator that is to be part of an elevator control
 * simulator.
 * 
 * @author Sonia Hassan-legault, Nicholas Porter, Karanvir Chaudhary, Christian Fisher
 * @version 4.0 2020-04-06
 *
 */
public class Elevator implements Runnable {
	private elevatorUDPThread udp;
	private int currentFloor, requestedFloor;
	private Direction dir;
	private int elID;
	private String Error;
	private boolean doorsOpen = false;
	private ElevatorStateMachine currState;

	/**
	 * Constructor for the Elevator class
	 * 
	 * @param elID - Sets this elevator's ID
	 */
	public Elevator(int elID) {
		this.elID = elID;
		requestedFloor = 0;
		currentFloor = 0;
		dir = Direction.IDLE;
		currState = ElevatorStateMachine.CurrFloorDoorsClosed; // initialize elevator state to current floor with doors
																// closed
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
		if (!Error.equals("hard")) { // If a hard error is present, dont update the floor to simulate stuck elevator
			if (this.getDirection() == Direction.UP) {
				this.currentFloor++;
			} else if (this.getDirection() == Direction.DOWN) {
				this.currentFloor--;
			}
		}

	}

	/**
	 * Getter for requestedFloor
	 * 
	 * @return requestedFloor
	 */
	public synchronized int getRequestedFloor() {
		return requestedFloor;
	}

	/**
	 * Setter for requestedFloor
	 * 
	 * @param requestedFloor Sets the requestedFloor to requestedFloor
	 */
	public synchronized void setRequestedFloor(int requestedFloor) {
		this.requestedFloor = requestedFloor;
	}

	/** Getter for doorsOpen
	 * 
	 * @return doorsOpen
	 */
	public boolean isDoorsOpen() {
		return doorsOpen;
	}
	/**Setter for doorsOpen
	 * 
	 * @param doorsOpen sets doorsOpen to doorsOpen
	 */
	public void setDoorsOpen(boolean doorsOpen) {
		this.doorsOpen = doorsOpen;
	}
	/**Setter for Error 
	 *  
	 * @return Error
	 */
	public String getError() {
		return Error;
	}
	/**Getter for elevatorID
	 * 
	 * @return elID
	 */
	public int getElID() {
		return elID;
	}

	/**
	 * 
	 * @return Direction elevator is traveling
	 */
	public Direction getDirection() {
		return this.dir;
	}

	/**
	 * Set the direction the elevator is traveling
	 * 
	 * @param d (Direction)
	 */
	public void setDirection(Direction d) {
		this.dir = d;
	}

	/**
	 * 
	 * @author Nicholas Porter State machine for the elevator using Java enums 4
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
		transientError {
			@Override
			public ElevatorStateMachine nextState() {
				return ArriveReqFloor;
			}
		},

		hardState {
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

					int diff = this.getCurrentFloor() - this.getRequestedFloor(); // Calculate difference between
																					// current location and destination
					if (diff > 0) {// if going down
						this.setDirection(Direction.DOWN);
						try {
							this.move(); // Move down 1 floor
							System.out.println("Elevator #" + this.elID + " is moving. Current Floor:  "
									+ this.currentFloor + " Destination: " + this.requestedFloor);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else if (diff < 0) {// if going up
						this.setDirection(Direction.UP);
						try {
							this.move();// move up one floor
							System.out.println("Elevator #" + this.elID + " is moving. Current Floor: "
									+ this.currentFloor + " Destination: " + this.requestedFloor);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						System.out.println("Elevator #" + this.elID + " has reached the requested floor (Floor #:"
								+ this.requestedFloor + ")");
						this.setDirection(Direction.IDLE);
						currState = currState.nextState();
						break;
					}

					break;
				}

				case hardState: {
					System.out.println("Elevator #:" + this.elID + "has experienced a hard fault error.");
					try {
						Thread.sleep(Long.MAX_VALUE); // Sleep for a very long time. This is to simulate a irrecoverable
														// fault. The elevator will stay in this state forever
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					currState = currState.nextState(); // the next state is hardState, so it will never leave this
														// state.
					break;
				}

				case transientError: {
					try {
						Thread.sleep(10000); // Simulates the period in which the error is being fixed
					} catch (InterruptedException e) {
						System.out.println(e.toString());
					}
					this.setError("null"); // Clear the error
					currState = currState.nextState(); // Set the state to the next state
					break;
				}

				case ArriveReqFloor: {
					try {
						this.setDoors(true); // Try to open doors
						Thread.sleep(1100);
						if (!this.isDoorsOpen()) {// If the doors didn't open
							currState = ElevatorStateMachine.transientError;// move to transient error state
							System.out.println("Elevator #" + this.getElID() + " in transient fault state");
							break;
						}
					} catch (InterruptedException e) {
						System.out.println("Door open Timer interrupted. Who did that?");
					}

					currState = currState.nextState();// Move to next state
					break;

				}

				case ReqFloorDoorsOpened: {
					try {
						this.setDoors(false);// Try to close doors
						Thread.sleep(1100);// Wait the until doors close
						if (this.isDoorsOpen()) {// if the doors didn't close
							currState = ElevatorStateMachine.transientError;// move to transient error state
							System.out.println("Elevator #" + this.getElID() + " in transient fault state");
							break;
						}
					} catch (InterruptedException e) {
						System.out.println("Door close Timer interrupted. Who did that?");

					}
					currState = currState.nextState();// Move to next state
					completeMove(elID, currentFloor, Error); // Update the scheduler that this elevator has completed
																// its move.
					break;

				}
			}
		}
	}

	/*
	 * Method moves the elevator either up or down 1 floor. This method checks for
	 * hard faults
	 * 
	 * @throws InterruptedException
	 */
	private void move() throws InterruptedException {
		int startFloor = this.getCurrentFloor(); // Saves the position of the elevator before attempting a move.
		this.updateCurrentFloor();
		Thread.sleep(3000);
		if (this.getCurrentFloor() == startFloor) { // If the elevator has not moved after 3 seconds, it is stuck and
													// therefore is hard faulting
			System.out.println("Elevator #" + this.getElID() + "in hard fault state");
			currState = ElevatorStateMachine.hardState;
		}

	}

	/*
	 * Completing a move asynchronously so that it can return back to state machine
	 */
	private void completeMove(int elID, int currentFloor, String errorMessage) {
		new Thread() {
			@Override
			public void run() {
				udp.completeMove(elID, currentFloor, errorMessage);
			}
		}.start();
	}

	/*
	 * This method opens or closes the doors. If there is a transient error, the
	 * method will fail
	 * 
	 * @param open true for opening doors, false for closing doors
	 */
	private void setDoors(boolean open) {
		if (!Error.equals("transient")) { // If the error is not transient
			this.setDoorsOpen(open); // setDoors
		}

	}

	/**
	 * Sets the errorMessage for the elevator
	 * 
	 * @param String - errorMessage is the string that the Error is to be set to
	 */
	public void setError(String errorMessage) {
		this.Error = errorMessage;
		System.out.println("Elevator #" + this.getElID() + " Error Status: " + errorMessage);

	}
}
