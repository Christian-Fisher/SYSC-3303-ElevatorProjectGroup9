import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

	private Scheduler scheduler; // Contains the reference to the scheduler
	LinkedList<RequestData> dataArray = new LinkedList<RequestData>(); // Holds all the data read from the file

	public FloorSubsystem(Scheduler scheduler) {

		this.scheduler = scheduler;
		readDataFromFile();
	}

	/**
	 * @Override This method will be run when this thread is started. Run will
	 *           iterate through the dataArray and send every item to the scheduler.
	 */
	public synchronized void run() {
		while (true) { // Ensures the thread loops through the whole method infinitly
			while (!dataArray.isEmpty()) { // Iterates through the linkedlist
				
				scheduler.placeRequest(dataArray.pop()); // Sends the request to the scheduler
				
			}
				while (scheduler.isCompletedListEmpty()) { // Check to see if the scheduler has any completed requests to report
					try {
						wait(); // Wait until there are requests to report
					} catch (InterruptedException e) {

					}
				}
				System.out.println(scheduler.getCompletedRequest() + " has been completed!"); // Print the request to the screen
			}
		}

	

	/**
	 * readDataFromFile takes all requests from the inputFile.txt. The method will
	 * then take the read string and convert it into a RequestData stucture.
	 * 
	 */
	@SuppressWarnings("deprecation")
	private void readDataFromFile() {
		File inputFile = new File("inputFile.txt"); // Creates the file. For testing the file is inputFile.txt
		try {
			Scanner fileReader = new Scanner(inputFile); // Creates a scanner to read the file
			while (fileReader.hasNextLine()) { // Iterates through the file
				String line = fileReader.nextLine();
				String[] splitLine = line.split(" "); // Splits each catagory into an array of strings
				Direction move = Direction.DOWN;
				if (splitLine[2].equals("Up")) { // Checks the direction in the request, and adds the RequestData object  to the arrayList based on the Direction
					move = Direction.UP;
				}
				Date inputTime = new SimpleDateFormat("HH:mm:ss.SSS").parse(splitLine[0]);
				Date date = new Date(); 
				date.setHours(inputTime.getHours());
				date.setMinutes(inputTime.getMinutes());
				date.setSeconds(inputTime.getSeconds());


				dataArray.add(new RequestData(date, Integer.parseInt(splitLine[1]), move, Integer.parseInt(splitLine[3]))); // Creates the RequestData object with the input from the text file

			}
			fileReader.close();// Closes the file
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
