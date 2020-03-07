

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * 
 * @author Nicholas Porter
 * @version 1.0
 *
 *	RequestData models the time, floor number
 *	and elevator 
 */
public class RequestData implements Serializable {
	Direction move;
	private static final long serialVersionUID = 1L;
	private int currentFloor;
	private Date time;
	private int requestFloor;
	private int elevatorID;
	
	public RequestData(Date time, int currentFloor, Direction direction, int requestFloor) {
		this.time = time;
		this.currentFloor = currentFloor;
		this.move = direction;
		this.requestFloor = requestFloor;
		this.elevatorID = 0;
	}
	
	/**
	 * get the LocalDateTime value
	 * @return time
	 */
	public Date getTime() {
		return this.time;
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
	
	/**
	 * Take a string as a parameter, parse it and create
	 * a new RequestData object
	 * @param str
	 * @throws ParseException
	 */
	public RequestData parse(String str) throws ParseException {
		StringTokenizer requestData = new StringTokenizer(str, " ");
		ArrayList<String> elements = new ArrayList<>();
		
		while(requestData.hasMoreTokens()) {
			elements.add(requestData.nextToken());
		}
		
		if(elements.size() != 4) {
			System.out.println("There must be 4 elements to create new RequestData object");
			return null;
		}
		
		Date parsedTime = new SimpleDateFormat("HH:mm:ss.SSS").parse(elements.get(0));
		int parsedCurrentFloor = Integer.parseInt(elements.get(1));
		Direction parsedDirection = Direction.valueOf(elements.get(2));
		int parsedRequestFloor = Integer.parseInt(elements.get(3));
		
		RequestData rd = new RequestData(parsedTime, parsedCurrentFloor, parsedDirection, parsedRequestFloor);
		return rd;
	}
	
	public int getElevatorID() {
		return elevatorID;
	}

	public void setElevatorID(int elevatorID) {
		this.elevatorID = elevatorID;
	}

	
	/**
	 * return the RequestData object as
	 * a string value
	 */
	@Override
	public String toString() {
		String s = new String();
		s += this.getTime() + " " + this.getCurrentFloor() + " " + this.getDirection() + " " + this.getRequestedFloor();
		return s;
	}
	

}
