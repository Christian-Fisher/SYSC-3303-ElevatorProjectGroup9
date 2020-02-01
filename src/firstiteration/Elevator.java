package firstiteration;

public class Elevator implements Runnable{
	
	private Scheduler scheduler;
	
	public Elevator(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public void run() {
		while(true) {
			//Elevator calling the scheduler
			FloorSubSystem floorSubSys = new FloorSubSystem(scheduler.getInfo().getTime(), scheduler.getInfo().getFloorNum(),
					scheduler.getInfo().getDirectionButton(), scheduler.getInfo().getFloorButton());
			//Elevator sends the info back
			scheduler.sendInfo(floorSubSys);
		}
		
	}

}
