import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class FloorSubsystem implements Runnable {

	private Scheduler scheduler;
	ArrayList<RequestData> dataArray = new ArrayList<RequestData>();
	public FloorSubsystem(Scheduler scheduler) {

		this.scheduler = scheduler;
		readDataFromFile();
	}

	@Override
	public void run() {
		Iterator<RequestData> dataArrayIterator = dataArray.iterator();
		while(dataArrayIterator.hasNext()) {
			scheduler.placeRequest(dataArrayIterator.next());			
			
		}
		
	}

	private void readDataFromFile() {
		File inputFile = new File("inputFile.txt");
		try {
			Scanner fileReader = new Scanner(inputFile);
			while(fileReader.hasNextLine()) {
				String line = fileReader.nextLine();
				String[] splitLine = line.split(" ");
				if(splitLine[2].equals("Up")) {
					dataArray.add(new RequestData(new SimpleDateFormat("HH:mm:ss.SSS").parse(splitLine[0]), Integer.parseInt(splitLine[1]), Direction.UP, Integer.parseInt(splitLine[3])));
				}else if(splitLine[2].equals("Down")) {
					dataArray.add(new RequestData(new SimpleDateFormat("HH:mm:ss.SSS").parse(splitLine[0]), Integer.parseInt(splitLine[1]), Direction.DOWN, Integer.parseInt(splitLine[3])));
				}
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
