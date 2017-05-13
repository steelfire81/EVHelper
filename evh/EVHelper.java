package evh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Scanner;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class EVHelper {

	private static final String WINDOW_NAME_LOAD = "Load File";
	private static final String WINDOW_NAME_FNF = "File Not Found";
	
	private static final String MSG_LOAD_FILE = "Load existing stat spread?";
	private static final String MSG_NEW_NAME = "Please name the new Pokemon";
	private static final String MSG_ERR_FNF = "ERROR: File not found";
	
	private static final String POKEMON_FILE_NAME = "evlist2";
	private static final int NUM_STATS = 6;
	
	private static final String MSG_ERR_NULLPOINT = "ERROR: Pokelist file lied about number of pokemon!!!";
	
	public static void main(String[] args) throws FileNotFoundException
	{
		InputStream myFile = EVHelper.class.getResourceAsStream(POKEMON_FILE_NAME);
		
		// Initialize list of Pokemon
		Scanner fileScan = new Scanner(myFile);
		ArrayList<EVPokemon> pokeList = new ArrayList<EVPokemon>();
		int index = 0;
		
		try
		{
			while(fileScan.hasNextLine())
			{
				String name = fileScan.next();
				int[] yield = new int[NUM_STATS];
				
				for(int i = 0; i < yield.length; i++)
					yield[i] = fileScan.nextInt();
				
				pokeList.add(new EVPokemon(name, yield));
				index++;
			}
		}
		catch(NullPointerException e)
		{
			System.err.println(MSG_ERR_NULLPOINT);
			System.exit(1);
		}
		
		// Sort list of Pokemon
		Collections.sort(pokeList);
		
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
				
				try
				{
					EVTrainingPokemon trainerPokemon = new EVTrainingPokemon(pokeFile);
					new EVWindow(pokeList, trainerPokemon, pokeFile);
				}
				catch(IOException ioe)
				{
					JOptionPane.showMessageDialog(null, MSG_ERR_FNF);
					System.exit(1);
				}
			}
			
		}
		else // Create new Pokemon
		{
			String name = JOptionPane.showInputDialog(MSG_NEW_NAME);
			
			if(name == null)
				System.exit(0);
			
			EVTrainingPokemon trainerPokemon = new EVTrainingPokemon(name);
			
			new EVWindow(pokeList, trainerPokemon, null);
		}
	}
}
