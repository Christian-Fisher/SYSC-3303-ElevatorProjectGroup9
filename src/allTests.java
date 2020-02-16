import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
/**
 * All tests
 * @author Karanvir, Dhyan
 * @version 2.0 2020-02-15
 *
 */
class allTests {
	Scheduler scheduler;
	Thread floorThread;
	Thread elevatorThread;
	FloorSubsystem floorSubsystem;
	Elevator elevator;
	RequestData request;

	@BeforeEach
	void setUp() throws Exception {
		scheduler = new Scheduler();	
		floorSubsystem = new FloorSubsystem(scheduler);
		elevator = new Elevator(scheduler);
		floorThread = new Thread(new FloorSubsystem(scheduler));	
		elevatorThread = new Thread(new Elevator(scheduler));
		request = new RequestData(new Date(), 1, Direction.UP, 3);
		
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
	
	/**
	 * Check if the elevator is in the correct state based on the state diagram
	 */
	@Test
	void testElevatorStates() {
		assertEquals(Elevator.ElevatorStateMachine.CurrFloorDoorsClosed, elevator.currState);
		
		elevator.currState = elevator.currState.nextState();
		assertEquals(Elevator.ElevatorStateMachine.Moving1, elevator.currState);
		
		elevator.currState = elevator.currState.nextState();
		assertEquals(Elevator.ElevatorStateMachine.ArriveReqFloor, elevator.currState);
		
		elevator.currState = elevator.currState.nextState();
		assertEquals(Elevator.ElevatorStateMachine.ReqFloorDoorsOpened, elevator.currState);
		
		elevator.currState = elevator.currState.nextState();
		assertEquals(Elevator.ElevatorStateMachine.Moving2, elevator.currState);
		
		elevator.currState = elevator.currState.nextState();
		assertEquals(Elevator.ElevatorStateMachine.ArriveDestFloor, elevator.currState);
		
		elevator.currState = elevator.currState.nextState();
		assertEquals(Elevator.ElevatorStateMachine.DestFloorDoorsOpened, elevator.currState);
	}
	
	/**
	 * Check if the scheduler is in the correct state based on the state diagram
	 */
	@Test
	void testSchedulerStatesOfOneRequest() {
		assertEquals(Scheduler.schedulerStateMachine.noRequests, scheduler.currentState);
		scheduler.currentState = scheduler.currentState.nextState();
		assertEquals(Scheduler.schedulerStateMachine.uncompletedRequests, scheduler.currentState);
		scheduler.currentState = scheduler.currentState.nextState();
		assertEquals(Scheduler.schedulerStateMachine.completedRequest, scheduler.currentState);
		scheduler.currentState = scheduler.currentState.nextState();
		assertEquals(Scheduler.schedulerStateMachine.noRequests, scheduler.currentState);		
	}
	
	/**
	 * Check if the scheduler is in the correct state based on the state diagram
	 */
	@Test
	void testSchedulerStatesOfMultipleRequest() {
		assertEquals(Scheduler.schedulerStateMachine.noRequests, scheduler.currentState);
		
		scheduler.currentState = scheduler.currentState.nextState();
		assertEquals(Scheduler.schedulerStateMachine.uncompletedRequests, scheduler.currentState);
		
		scheduler.currentState = scheduler.currentState.nextState();
		assertEquals(Scheduler.schedulerStateMachine.completedRequest, scheduler.currentState);
		
		scheduler.placeRequest(request);
		
		scheduler.currentState = scheduler.currentState.nextState();
		assertEquals(Scheduler.schedulerStateMachine.uncompletedRequests, scheduler.currentState);		
	}
	
	
}
