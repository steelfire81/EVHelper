package evh;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Represents the Pokemon the user is training
 */
public class EVTrainingPokemon {

	// CONSTANTS
	private static final int[][] EMPTY_STAT_SET = {{0,0,0,0,0,0}, {0,0,0,0,0,0}};
	
	// DATA MEMBERS
	// TODO: More information about user's pokemon (species, etc.)
	private String name;
	private int[][] evSpread;
	private boolean pokerus;
	
	/**
	 * Name constructor - instantiates a new Pokemon with a given name
	 * @param name pokemon's name
	 */
	public EVTrainingPokemon(String n)
	{
		name = n;
		evSpread = EMPTY_STAT_SET;
		pokerus = false;
	}
	
	/**
	 * File constructor - instantiates a Pokemon with saved info
	 * @param pokefile file containing saved Pokemon information
	 */
	public EVTrainingPokemon(File pokefile) throws IOException
	{
		evSpread = EMPTY_STAT_SET;
		Scanner fileScan = new Scanner(pokefile);
		name = fileScan.nextLine();
		
		// Scan in total, goal lines
		for(int i = 0; i < evSpread[0].length; i++)
			evSpread[0][i] = fileScan.nextInt();
		for(int i = 0; i < evSpread[1].length; i++)
			evSpread[1][i] = fileScan.nextInt();
		
		pokerus = fileScan.nextInt() == 1;	
	}
	
	/**
	 * get this Pokemon's name
	 * @return this Pokemon's name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * get this Pokemon's EV spread
	 * @return this Pokemon's EV spread
	 */
	public int[][] getEVSpread()
	{
		return evSpread;
	}
	
	/**
	 * get whether or not this Pokemon has Pokerus
	 * @return true if this Pokemon has Pokerus, false otherwise
	 */
	public boolean hasPokerus()
	{
		return pokerus;
	}
}
