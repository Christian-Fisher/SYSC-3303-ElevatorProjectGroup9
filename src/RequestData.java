import java.io.Serializable;

/**
 * 
 * @author Nicholas Porter
 * @version 1.0
 * 
 * @author Sonia Hassan-Legault
 * @version 2.0 Error message now added
 *
 *	RequestData models the time, floor number
 *	and elevator 
 */
public class RequestData implements Serializable {
	Direction move;
	private static final long serialVersionUID = 1L;
	private int currentFloor;
	private int delay;
	private int requestFloor;
	private int elevatorID;
	private String errorMessage;
	
	/**
	 * Constructor for RequestData with parameters.
	 * 
	 * @param delay - amount of time the system waits before sending the request
	 * @param currentFloor - the floor where the request was made
	 * @param direction - whether the elevator will need to go Up or Down
	 * @param requestFloor - the desired destination floor
	 * @param errorMessage - can either be "transient" or "hard", null otherwise
	 */
	public RequestData(int delay, int currentFloor, Direction direction, int requestFloor, String errorMessage) {
		this.delay = delay;
		this.currentFloor = currentFloor;
		this.move = direction;
		this.requestFloor = requestFloor;
		this.elevatorID = -1;
		this.errorMessage = errorMessage;

	}
	
	/**
	 * Constructor for RequestData. Sets variables to default values.
	 */
	public RequestData() {
		this.delay =0;
		this.currentFloor = -1;
		this.move =Direction.IDLE;
		this.requestFloor = -1;
		this.errorMessage = null;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public int getElevatorID() {
		return elevatorID;
	}

	public void setElevatorID(int elevatorID) {
		this.elevatorID = elevatorID;
	}
	public Direction getMove() {
		return move;
	}

	public void setMove(Direction move) {
		this.move = move;
	}

	public int getRequestFloor() {
		return requestFloor;
	}

	public void setRequestFloor(int requestFloor) {
		this.requestFloor = requestFloor;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	/**
	 * get the LocalDateTime value
	 * @return time
	 */
	public int getDelay() {
		return this.delay;
	}
	
	/**
	 * get the int value 
	 * representing floor number
	 * @return floorNum
	 */
	public int getCurrentFloor() {
		return this.currentFloor;
	}

	/**
	 * get the int value
	 * representing the floor number
	 * pressed
	 * @return floorButton
	 */
	public int getRequestedFloor() {
		return this.requestFloor;
	}
	
	/**
	 * get the Direction value
	 * representing the state of the
	 * elevator (UP/DOWN) button
	 * @return button
	 */
	public Direction getDirection() {
		return this.move;
	}
}
