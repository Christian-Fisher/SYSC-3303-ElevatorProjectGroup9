import java.util.Date;

public class Elevator implements Runnable{
	
	private Scheduler scheduler;
	
	public Elevator(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public void run() {
		while(true) {
			//Elevator calling the scheduler
			RequestData requestData = scheduler.processRequest();
			
			int diff = requestData.getCurrentFloor() - requestData.getRequestedFloor();
			//Elevator sends the info back
			Date date = new Date(System.currentTimeMillis());
			scheduler.completeRequest(date, requestData.getRequestedFloor(), true);
		}
		
	}

}
