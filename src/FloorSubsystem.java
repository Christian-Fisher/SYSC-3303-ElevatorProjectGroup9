import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author Christian Fisher
 * @version 1.0 The Floor Subsystem is responsible for reading the requests from
 *          a textfile and sending these requests to the scheduler.
 * 
 * 
 */
public class FloorSubsystem implements Runnable {

	 // Contains the reference to the scheduler
	LinkedList<RequestData> dataArray = new LinkedList<RequestData>(); // Holds all the data read from the file
	floorSubsystemThread udpFloorSubsystemThread;
	public FloorSubsystem() {
		this.udpFloorSubsystemThread = new floorSubsystemThread(this);
		Thread floorUDP = new Thread(udpFloorSubsystemThread);
		floorUDP.setName("Floor UDP");
		floorUDP.start();
		readDataFromFile();
	}
	/**
	 * Returns dataArray, a Linked List, consisting of all the requests obtained from the input file 
	 * @return the dataArray 
	 */
	public LinkedList<RequestData> getdataArray() {
		return this.dataArray;
	}
	
	/**
	 * @Override This method will be run when this thread is started. Run will
	 *           iterate through the dataArray and send every item to the scheduler.
	 */
	public synchronized void run() {
			while (!dataArray.isEmpty()) { // Iterates through the linkedlist
				
				try {
					Thread.sleep(dataArray.get(0).getDelay()*1000);
				} catch(InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
				udpFloorSubsystemThread.sendRequest(dataArray.pop());
				//System.out.println("Next request: "+dataArray.peek());
			}

			}

	

	/**
	 * readDataFromFile takes all requests from the inputFile.txt. The method will
	 * then take the read string and convert it into a RequestData stucture.
	 * 
	 */
	private void readDataFromFile() {
		File inputFile = new File("inputFile.txt"); // Creates the file. For testing the file is inputFile.txt
		try {
			Scanner fileReader = new Scanner(inputFile); // Creates a scanner to read the file
			while (fileReader.hasNextLine()) { // Iterates through the file
				String line = fileReader.nextLine();
				String[] splitLine = line.split(" "); // Splits each category into an array of strings
				Direction move = Direction.DOWN;
				if (splitLine[1].equals("Up")) { // Checks the direction in the request, and adds the RequestData object  to the arrayList based on the Direction
					move = Direction.UP;
				}
				int delay = Integer.parseInt(splitLine[0]);

				dataArray.add(new RequestData(delay, Integer.parseInt(splitLine[2]), move, Integer.parseInt(splitLine[3]))); // Creates the RequestData object with the input from the text file

			}
			fileReader.close();// Closes the file
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

}
