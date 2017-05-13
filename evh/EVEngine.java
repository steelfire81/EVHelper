package evh;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class EVEngine implements ActionListener {

	// Constants
	private static final int ITEM_NONE = 0;
	private static final int ITEM_MACHO = 1;
	private static final int ITEM_HP = 2;
	private static final int ITEM_ATK = 3;
	private static final int ITEM_DEF = 4;
	private static final int ITEM_SPA = 5;
	private static final int ITEM_SPD = 6;
	private static final int ITEM_SPEED = 7;
	
	private static final int POWER_ITEM_BONUS = 4;
	private static final int MACHO_MULTIPLIER = 2;
	private static final int POKERUS_MULTIPLIER = 2;
	
	private static final int EV_MAX = 255;
	private static final int EV_TOTAL_MAX = 510;
	
	private static final String MSG_SAVE = "Save existing stat spread?";
	private static final String MSG_ERR_FNF = "ERROR: File not found";
	
	private static final String WINDOW_NAME_FNF = "File Not Found";
	private static final String WINDOW_NAME_SAVE = "Save Progress";
	
	// Data Members
	private EVWindow parent;
	private ArrayList<EVPokemon> pokemonList;
	private File saveFile;
	
	public EVEngine(EVWindow window, ArrayList<EVPokemon> pokeList, File pokeFile)
	{
		parent = window;
		pokemonList = pokeList;
		saveFile = pokeFile;
	}
	
	public void actionPerformed(ActionEvent e)
	{	
		// Check combo boxes
		for(int i = 0; i < parent.pokePanelComboBoxes.length; i++)
			if(e.getSource() == parent.pokePanelComboBoxes[i])
				updatePokePanel(i);
		
		// Check KO Buttons
		for(int i = 0; i < parent.buttonPokeKO.length; i++)
			if(e.getSource() == parent.buttonPokeKO[i])
				pokemonKO(i);
		
		// Check save button
		if(e.getSource() == parent.buttonSave)
			writeToFile();
		
		// Check load button
		if(e.getSource() == parent.buttonLoad)
			loadFile();
		
		// Check lock / unlock buttons
		if(e.getSource() == parent.buttonGoalLock || e.getSource() == parent.buttonTotalLock)
		{
			JButton source = (JButton) e.getSource();
			if(source == parent.buttonGoalLock)
				if(source.getText().equals(EVWindow.BUTTON_LOCK_TEXT))
					lockGoals();
				else
					unlockGoals();
			else if(source == parent.buttonTotalLock)
				if(source.getText().equals(EVWindow.BUTTON_LOCK_TEXT))
					lockValues();
				else
					unlockValues();
		}
		
			
	}
	
	// pokemonKO - knockout the selected pokemon (indicated by index)
	private void pokemonKO(int index)
	{
		int[] totalEVYield = new int[EVWindow.STAT_NAMES.length];
		for(int i = 0; i < totalEVYield.length; i++)
			totalEVYield[i] = 0;
		
		// First, apply from Pokemon KO'd
		int selection = parent.pokePanelComboBoxes[index].getSelectedIndex();
		for(int i = 0; i < totalEVYield.length; i++)
			totalEVYield[i] += pokemonList.get(selection).getEVYield()[i];
		
		// Next, apply stat item bonuses
		if(parent.itemButtons[ITEM_HP].isSelected())
			totalEVYield[0] += POWER_ITEM_BONUS;
		else if(parent.itemButtons[ITEM_ATK].isSelected())
			totalEVYield[1] += POWER_ITEM_BONUS;
		else if(parent.itemButtons[ITEM_DEF].isSelected())
			totalEVYield[2] += POWER_ITEM_BONUS;
		else if(parent.itemButtons[ITEM_SPA].isSelected())
			totalEVYield[3] += POWER_ITEM_BONUS;
		else if(parent.itemButtons[ITEM_SPD].isSelected())
			totalEVYield[4] += POWER_ITEM_BONUS;
		else if(parent.itemButtons[ITEM_SPEED].isSelected())
			totalEVYield[5] += POWER_ITEM_BONUS;
		
		// Check for Macho Brace
		if(parent.itemButtons[ITEM_MACHO].isSelected())
			for(int i = 0; i < totalEVYield.length; i++)
				totalEVYield[i] *= MACHO_MULTIPLIER;
		
		// Check for Pokerus
		if(parent.pokerusCheckbox.isSelected())
			for(int i = 0; i < totalEVYield.length; i++)
				totalEVYield[i] *= POKERUS_MULTIPLIER;
		
		// Add to all totals and confirm that values are valid
		for(int i = 1; i < parent.evTotals.length; i++)
		{
			int newValue = Integer.parseInt(parent.evTotals[i].getText()) + totalEVYield[i - 1];
			
			// Check if newValue is over maximum
			if(newValue > EV_MAX)
				newValue = EV_MAX;
			parent.evTotals[i].setText(Integer.toString(newValue));
			
			// Check if EV total is over maximum total
			int evTotal = 0;
			for(int j = 1; j < parent.evTotals.length; j++)
				evTotal += Integer.parseInt(parent.evTotals[j].getText());
			while(evTotal > EV_TOTAL_MAX)
			{
				newValue--;
				evTotal--;
			}
			parent.evTotals[i].setText(Integer.toString(newValue));
		}
	}
	
	// updateAllPokePanels - as the name suggests, updates all pokepanels
	public void updateAllPokePanels()
	{
		for(int i = 0; i < parent.pokePanels.length; i++)
			updatePokePanel(i);
	}
	
	// updatePokePanel - update the pokepanel with the given index when combo box is changed
	private void updatePokePanel(int index)
	{
		int selection = parent.pokePanelComboBoxes[index].getSelectedIndex();
		int[] evYield = pokemonList.get(selection).getEVYield();
		
		for(int i = 0; i < parent.pokePanelStatYields[index].length; i++)
			parent.pokePanelStatYields[index][i].setText(Integer.toString(evYield[i]));
	}
	
	// lockGoals - set goal fields to uneditable
	private void lockGoals()
	{
		for(int i = 1; i < parent.evGoals.length; i++)
			parent.evGoals[i].setEditable(false);
		
		parent.buttonGoalLock.setText(EVWindow.BUTTON_UNLOCK_TEXT);
	}
	
	// unlockGoals - set goal fields to editable
	private void unlockGoals()
	{
		for(int i = 1; i < parent.evGoals.length; i++)
			parent.evGoals[i].setEditable(true);
		
		parent.buttonGoalLock.setText(EVWindow.BUTTON_LOCK_TEXT);
	}
	
	// lockValues - set value fields to uneditable
	private void lockValues()
	{
		for(int i = 1; i < parent.evTotals.length; i++)
			parent.evTotals[i].setEditable(false);
		
		parent.buttonTotalLock.setText(EVWindow.BUTTON_UNLOCK_TEXT);
	}
	
	// unlockValues - set values fields to editable
	private void unlockValues()
	{
		for(int i = 1; i < parent.evTotals.length; i++)
			parent.evTotals[i].setEditable(true);
		
		parent.buttonTotalLock.setText(EVWindow.BUTTON_LOCK_TEXT);
	}
	
	// writeToFile - write data to a file
	private void writeToFile()
	{
		if(saveFile != null)
		{
			try
			{
				PrintWriter fileWriter = new PrintWriter(saveFile);
				fileWriter.println(parent.nameField.getText());
				
				// Print stat spread
				for(int i = 0; i < EVWindow.STAT_NAMES.length; i++)
					fileWriter.print(parent.evTotals[i + 1].getText() + " ");
				
				fileWriter.println();
				
				for(int i = 0; i < EVWindow.STAT_NAMES.length; i++)
					fileWriter.print(parent.evGoals[i + 1].getText() + " ");
				
				fileWriter.println();
				
				// Print 1 for Pokerus
				if(parent.pokerusCheckbox.isSelected())
					fileWriter.print("1");
				else
					fileWriter.print("0");
				
				fileWriter.close();
			}
			catch(FileNotFoundException e)
			{
				
			}
			
		}
		else // Make new file
		{
			JFileChooser selector = new JFileChooser();
			selector.setCurrentDirectory(null);
			int result = selector.showSaveDialog(parent.mainPanel);
			
			if(result == JFileChooser.APPROVE_OPTION)
			{
				saveFile = selector.getSelectedFile();
				writeToFile();
			}
		}			
	}
	
	// loadFile - load a pokemon EV file to the window
	private void loadFile()
	{
		// TODO: Ask user to save current data
		int save = JOptionPane.showConfirmDialog(parent.mainPanel, MSG_SAVE, WINDOW_NAME_SAVE, JOptionPane.YES_NO_CANCEL_OPTION);
		if(save == JOptionPane.CANCEL_OPTION)
			return;
		else if(save == JOptionPane.YES_OPTION)
		{
			writeToFile();
		}
		
		// ***********************************
		JFileChooser selector = new JFileChooser();
		selector.setCurrentDirectory(null);
		int result = selector.showOpenDialog(parent.mainPanel);
		
		if(result == JFileChooser.APPROVE_OPTION)
		{
			File loadFile = selector.getSelectedFile();
			
			try
			{
				Scanner fileScan = new Scanner(loadFile);
				String name = fileScan.nextLine();
				
				// Scan in total, goal lines
				int[][] evSpread = new int[2][EVWindow.STAT_NAMES.length];
				
				for(int i = 0; i < evSpread[0].length; i++)
					evSpread[0][i] = fileScan.nextInt();
				for(int i = 0; i < evSpread[1].length; i++)
					evSpread[1][i] = fileScan.nextInt();
				
				boolean pokerus = fileScan.nextInt() == 1;
				
				// Set up window
				saveFile = loadFile;
				parent.nameField.setText(name);
				parent.itemButtons[ITEM_NONE].setSelected(true);
				parent.pokerusCheckbox.setSelected(pokerus);
				
				// Set up stats in window
				for(int i = 1; i < parent.evTotals.length; i++)
					parent.evTotals[i].setText(Integer.toString(evSpread[0][i - 1]));
				for(int i = 1; i < parent.evGoals.length; i++)
					parent.evGoals[i].setText(Integer.toString(evSpread[1][i - 1]));
			}
			catch(FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(parent.mainPanel, MSG_ERR_FNF, WINDOW_NAME_FNF, JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
}
