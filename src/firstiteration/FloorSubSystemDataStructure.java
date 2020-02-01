package firstiteration;

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
 *	FloorSubSystem models the time, floor number
 *	and elevator 
 */
public class FloorSubSystemDataStructure implements Serializable {
	enum Direction {UP, DOWN, IDLE}
	Direction button;
	private static final long serialVersionUID = 1L;
	private int floorNum;
	private Date time;
	private int floorButton;
	
	public FloorSubSystemDataStructure(Date time, int floorNum, Direction directionButton, int floorButton) {
		this.time = time;
		this.floorNum = floorNum;
		this.button = directionButton;
		this.floorButton = floorButton;
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
	public int getFloorNum() {
		return this.floorNum;
	}
	
	/**
	 * get the int value
	 * representing the floor number
	 * pressed
	 * @return floorButton
	 */
	public int getFloorButton() {
		return this.floorButton;
	}
	
	/**
	 * get the Direction value
	 * representing the state of the
	 * elevator (UP/DOWN) button
	 * @return button
	 */
	public Direction getDirectionButton() {
		return this.button;
	}
	
	/**
	 * Take a string as a parameter, parse it and create
	 * a new FloorSubSystem object
	 * @param str
	 * @throws ParseException
	 */
	public FloorSubSystemDataStructure parseFloorSubSystemString(String str) throws ParseException {
		StringTokenizer floorSubSystem = new StringTokenizer(str, " ");
		ArrayList<String> elements = new ArrayList<>();
		
		while(floorSubSystem.hasMoreTokens()) {
			elements.add(floorSubSystem.nextToken());
		}
		
		if(elements.size() != 4) {
			System.out.println("There must be 4 elements to create new FloorSubSystem object");
			return null;
		}
		
		Date parsedTime = new SimpleDateFormat("HH:mm:ss.SSS").parse(elements.get(0));
		int parsedFloorNum = Integer.parseInt(elements.get(1));
		Direction parsedDirection = Direction.valueOf(elements.get(2));
		int parsedFloorButton = Integer.parseInt(elements.get(3));
		
		FloorSubSystemDataStructure fss = new FloorSubSystemDataStructure(parsedTime, parsedFloorNum, parsedDirection, parsedFloorButton);
		return fss;
	}
	
	/**
	 * return the FloorSubSystem object as
	 * a string value
	 */
	@Override
	public String toString() {
		String s = new String();
		s += this.getTime() + " " + this.getFloorNum() + " " + this.getDirectionButton() + " " + this.getFloorButton();
		return s;
	}
	

}
