package assignment1;

/**
 * 
 * @author Nicholas Porter
 * @version 1.0
 * 
 * The purpose of class Kitchen is to instantiate and start
 * threads agent, chef1, chef2, and chef3
 *
 */
public class Kitchen {

	public static void main(String[] args) {
		Table table = new Table();
		Thread chef1, chef2, chef3, agent;
		agent = new Thread(new Agent(table), "Agent");
		chef1 = new Thread(new Chef("jam", table), "Chef1");
		chef2 = new Thread(new Chef("peanut butter", table), "Chef2");
		chef3 = new Thread(new Chef("bread", table), "Chef3");
		
		agent.start();
		chef1.start();
		chef2.start();
		chef3.start();

	}

}
