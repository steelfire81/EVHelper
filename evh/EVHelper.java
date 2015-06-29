package evh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class EVHelper {

	private static final String WINDOW_NAME_LOAD = "Load File";
	private static final String WINDOW_NAME_FNF = "File Not Found";
	
	private static final String MSG_LOAD_FILE = "Load existing stat spread?";
	private static final String MSG_NEW_NAME = "Please name the new Pokemon";
	private static final String MSG_ERR_FNF = "ERROR: File not found";
	
	private static final String POKEMON_FILE_NAME = "evlist2";
	private static final int[][] EMPTY_STAT_SET = {{0,0,0,0,0,0}, {0,0,0,0,0,0}};
	private static final int NUM_STATS = 6;
	
	private static final String MSG_ERR_NULLPOINT = "ERROR: Pokelist file lied about number of pokemon!!!";
	
	public static void main(String[] args) throws FileNotFoundException
	{
		InputStream myFile = EVHelper.class.getResourceAsStream(POKEMON_FILE_NAME);
		
		// Initialize list of Pokemon
		Scanner fileScan = new Scanner(myFile);
		int numPokemon = fileScan.nextInt();
		EVPokemon[] pokeList = new EVPokemon[numPokemon];
		int index = 0;
		
		try
		{
			while(fileScan.hasNextLine())
			{
				String name = fileScan.next();
				int[] yield = new int[NUM_STATS];
				
				for(int i = 0; i < yield.length; i++)
					yield[i] = fileScan.nextInt();
				
				pokeList[index] = new EVPokemon(name, yield);
				index++;
			}
		}
		catch(NullPointerException e)
		{
			System.err.println(MSG_ERR_NULLPOINT);
			System.exit(1);
		}
		
		// Sort list of Pokemon
		pokesort(pokeList);
		
		// Load a pokemon (if the user wants)
		int load = JOptionPane.showConfirmDialog(null, MSG_LOAD_FILE, WINDOW_NAME_LOAD, JOptionPane.YES_NO_OPTION);
		if(load == JOptionPane.YES_OPTION)
		{
			JFileChooser selector = new JFileChooser();
			selector.setCurrentDirectory(null); // sets default directory to documents directory
			int result = selector.showOpenDialog(null);
			
			if(result == JFileChooser.APPROVE_OPTION)
			{
				File pokeFile = selector.getSelectedFile();
				loadFile(pokeFile, pokeList);
			}
			
		}
		else // Create new Pokemon
		{
			String name = JOptionPane.showInputDialog(MSG_NEW_NAME);
			
			if(name == null)
				System.exit(0);
			
			new EVWindow(pokeList, name, EMPTY_STAT_SET, false, null);
		}
	}
	
	// loadFile - load the selected file and create an appropriate EVWindow
	private static void loadFile(File pokeFile, EVPokemon[] pokeList)
	{
		try
		{
			Scanner fileScan = new Scanner(pokeFile);
			String name = fileScan.nextLine();
			
			// Scan in total, goal lines
			int[][] evSpread = new int[2][EVWindow.STAT_NAMES.length];
			
			for(int i = 0; i < evSpread[0].length; i++)
				evSpread[0][i] = fileScan.nextInt();
			for(int i = 0; i < evSpread[1].length; i++)
				evSpread[1][i] = fileScan.nextInt();
			
			boolean pokerus = fileScan.nextInt() == 1;
			
			// Initialize window
			new EVWindow(pokeList, name, evSpread, pokerus, pokeFile);
		}
		catch(FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(null, MSG_ERR_FNF, WINDOW_NAME_FNF, JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}
	
	
	
	// SORTING FUNCTIONS: Used to sort the list of Pokemon in alphabetical order
	private static void pokesort(EVPokemon[] pokemonList)
	{
		pokesort(pokemonList, 0, pokemonList.length - 1);
	}
	
	private static void pokesort(EVPokemon[] pokemonList, int start, int end)
	{
		if(start < end)
		{
			int pivot = pokepartition(pokemonList, start, end);
			pokesort(pokemonList, start, pivot - 1);
			pokesort(pokemonList, pivot + 1, end);
		}
	}
	
	private static int pokepartition(EVPokemon[] pokemonList, int start, int end)
	{
		int pivot = pokepivot(pokemonList, start, end);
		EVPokemon pivotPokemon = pokemonList[pivot];
		
		pokemonList[pivot] = pokemonList[end];
		pokemonList[end] = pivotPokemon;
		
		int curr = start;
		for(int i = start; i < end; i++)
			if(pokemonList[i].compareTo(pivotPokemon) <= 0)
			{
				EVPokemon temp = pokemonList[curr];
				pokemonList[curr] = pokemonList[i];
				pokemonList[i] = temp;
				curr++;
			}
		
		pokemonList[end] = pokemonList[curr];
		pokemonList[curr] = pivotPokemon;
		
		return curr;
	}
	
	private static int pokepivot(EVPokemon[] pokemonList, int start, int end)
	{
		// This is a lazy pivot selection but because it doesn't sort that many things it's passable
		return start;
	}
	
}
