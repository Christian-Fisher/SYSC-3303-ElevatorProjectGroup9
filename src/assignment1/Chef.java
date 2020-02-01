package assignment1;

/**
 * 
 * @author Nicholas Porter
 * @version 1.0
 *
 */
public class Chef implements Runnable {
	private String ingredient;
	private Table table;
	
	public Chef(String ingredient, Table table) {
		this.ingredient = ingredient;
		this.table = table;
	}
	
	public String getChefIngredient() {
		return this.ingredient;
	}
	
	@Override
	public void run() {
		while(true) {
			table.getIngredients(this.getChefIngredient());
			System.out.println("Total sandwiches are now: " + table.getTotalSandwiches());
			try {
	            Thread.sleep(10);
	        } catch (InterruptedException e) {}
		}
	}

}
