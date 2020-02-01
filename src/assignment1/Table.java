package assignment1;

import java.util.ArrayList;

/**
 * 
 * @author Nicholas Porter
 * @version 1.0
 * 
 * The purpose of class Table is to act as the thread-safe "box" when the producer (Agent)
 * can put an object (ingredients) into the box, and the consumer (Chef) can get that object from the box
 *
 */
public class Table {
	private boolean empty;
	private int totalSandwiches;
	private ArrayList<String> contents;
	
	public Table() {
		this.empty = true;
		this.totalSandwiches = 0;
		this.contents = new ArrayList<>();
	}
	
	/**
	 * The Agent passes an ArrayList<String> of two random ingredients
	 * If the table is empty, the Agent places these ingredients on the table
	 * Else, the Agent waits
	 * @param ingredients
	 */
	public synchronized void putIngredients(ArrayList<String> ingredients) {
		while (!empty) {
			try {
				wait();
			} catch (InterruptedException e) {return;}
		}
		contents = ingredients;
		System.out.println("Ingredients currently on the table:");
		System.out.println(this.getContents());
		empty = false;
		notifyAll();
	}
	
	/**
	 * A Chef passes their ingredient to the function
	 * If the table is not empty and both the ingredients on
	 * the table are different from the Chef's ingredient, the
	 * Chef will get those ingredients and make a sandwich
	 * Else, the Chef waits
	 * 
	 * @param ingredient
	 */
	public synchronized void getIngredients(String ingredient) {
		while (empty || this.getContents().contains(ingredient)) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		totalSandwiches++;
		contents.clear();
		empty = true;
		notifyAll();
	}
	
	public int getTotalSandwiches() {
		return this.totalSandwiches;
	}
	
	public ArrayList<String> getContents() {
		return contents;
	}

}
