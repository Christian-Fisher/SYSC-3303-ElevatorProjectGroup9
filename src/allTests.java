import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class allTests {
	Scheduler scheduler;
	Thread floorThread;
	Thread elevatorThread;
	FloorSubsystem floorSubsystem;
	Elevator elevator;

	@BeforeEach
	void setUp() throws Exception {
		scheduler = new Scheduler();	
		floorSubsystem = new FloorSubsystem(scheduler);
		elevator = new Elevator(scheduler);
		floorThread = new Thread(new FloorSubsystem(scheduler));	
		elevatorThread = new Thread(new Elevator(scheduler));
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	/**
	 * When the FloorSubsystem constructor is called, it uses the readInputFromFile() function
	 * and adds the requests from the inputFile.txt and adds it to a linked list called dataArray.
	 * Since the inputFile.txt only has two requests(entries), the test checks whether there are 2
	 * entries in the dataArray. 
	 */
	@Test
	void testreadDataFromFile() {	
		assertEquals(2, floorSubsystem.getdataArray().size(),"Size of dataArray should be 2." );
	}
	
	/**
	 * Checks if the getCurrentFloor function returns 0 when an elevator object is first constructed. 
	 */
	@Test
	void testgetCurrentFloor() {
		assertEquals(0,elevator.getCurrentFloor(),"The current floor should be 0");
	}
	
	/**
	 * Check if the setCurrentFloor function is able to update the floor number of an elevator. 
	 * The elevator is originally at floor zero. setCurrentFloor() is called to set the floor number to 2. 
	 */
	@Test
	void testSetCurrentFloor() {
		elevator.setCurrentFloor(2);
		assertEquals(2,elevator.getCurrentFloor(),"The floor should now be set to 2.");
	}
	
	
}
