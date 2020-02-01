package assignment1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * 
 * @author Nicholas Porter
 * @version 1.0
 * 
 * The purpose of class Agent is that of the producer in the producer/consumer
 * relationship. The Agent has an unlimited supply of all three ingredients needed for
 * a sandwich, and when the table is empty will randomly place two of the three ingredients 
 * on the table
 *
 */
public class Agent implements Runnable {
	
	private Table table;
	// an ArrayList of Strings that contains all the ingredients needed to create a sandwich
	private ArrayList<String> ingredients;
	// an ArrayList containing two random ingredients out of the three needed to create a sandwich
	private ArrayList<String> twoIngredients;
	private int randomNum1;
	private int randomNum2;
	// final int NUM_SANDWICHES determines how many sandwiches will be made and consumed
	private final int NUM_SANDWICHES = 20;
	
	public Agent(Table table) {
		this.table = table;
		ingredients = new ArrayList<>(Arrays.asList("jam", "peanut butter", "bread"));
	}

	@Override
	public void run() {
		for(int i = 0; i < NUM_SANDWICHES; i++) {
			
			Random r = new Random();
			randomNum1 = r.nextInt(3);
			randomNum2 = randomNum1;
			
			// ensure that randomNum2 is not the same integer value as randomNum1
			while (randomNum2 == randomNum1) {
				randomNum2 = r.nextInt(3);
			}
			
			twoIngredients = new ArrayList<String>();
			twoIngredients.add(ingredients.get(randomNum1));
			twoIngredients.add(ingredients.get(randomNum2));
			
			table.putIngredients(twoIngredients);
			try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
		}
		
	}

}
