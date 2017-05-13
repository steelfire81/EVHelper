package evh;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class EVWindow {

	// Constants
	private static final String WINDOW_NAME = "Pokemon EV Helper: ";
	private static final String WINDOW_ICON = "icon.png";
	private static final int WINDOW_WIDTH = 750;
	private static final int WINDOW_HEIGHT = 275;
	
	private static final String MSG_NAME = "NAME:";
	private static final String MSG_ERR_ICON = "ERROR: Icon not found";
	
	private static final int ITEM_PANEL_ROWS = 2;
	private static final String[] ITEM_NAMES = {"NONE", "Macho Brace (2x)", "Power Weight (+4 HP)", "Power Bracer (+4 ATK)",
												"Power Belt (+4 DEF)", "Power Lens (+4 SpA)", "Power Band (+4 SpD)", "Power Anklet (+4 Spe)"};
	
	private static final String POKERUS_NAME = "Pokerus";
	
	private static final String STAT_NAMES_PRETEXT = "";
	public static final String[] STAT_NAMES = {"HP", "Atk", "Def", "SpA", "SpD", "Speed"};
	private static final String STAT_VALUES_PRETEXT = "EV VALUES";
	private static final String STAT_GOALS_PRETEXT = "EV GOALS";
	private static final int STAT_PANEL_HEIGHT = 3;
	private static final boolean STAT_VALS_START_EDITABLE = true; // whether or not you can edit stat values from the start
	private static final boolean STAT_GOALS_START_EDITABLE = true; // whether or not you can edit stat goals from the start
	
	private static final int POKE_PANEL_MAX = 3; // number of pokepanels to show in the center
	
	public static final String BUTTON_LOCK_TEXT = "LOCK";
	public static final String BUTTON_UNLOCK_TEXT = "UNLOCK";
	private static final String BUTTON_KO_TEXT = "KO";
	private static final String BUTTON_SAVE_TEXT = "SAVE";
	private static final String BUTTON_LOAD_TEXT = "LOAD";
	
	// Window elements
	JPanel mainPanel;
	
	// North Panel: Possible Items
	JPanel northPanel;
	JPanel itemPanel;
	JPanel namePanel;
	JCheckBox pokerusCheckbox;
	JRadioButton[] itemButtons;
	JTextField nameField;
	JButton buttonSave;
	JButton buttonLoad;
	
	// Central Panel: List selected Pokemon
	JPanel centralPanel;
	JPanel[] pokePanels;
	JTextField[] pokePanelNames;
	JComboBox[] pokePanelComboBoxes;
	JTextField[][] pokePanelStatYields;
	JButton[] buttonPokeKO;
	
	// South Panel: Grid of numbers
	JPanel statPanel;
	JTextField[] statNames;
	JButton buttonTotalLock;
	JButton buttonGoalLock;
	JTextField[] evTotals;
	JTextField[] evGoals;
	
	
	// Data Members
	private ArrayList<EVPokemon> pokemonList;
	private String[] pokemonNames;
	private EVEngine engine;
	
	
	public EVWindow(ArrayList<EVPokemon> pokeList, EVTrainingPokemon trainerPokemon, File originFile)
	{
		// Copy data
		pokemonList = pokeList;
		pokemonNames = new String[pokeList.size()];
		for(int i = 0; i < pokeList.size(); i++)
			pokemonNames[i] = pokeList.get(i).getName();
		
		// Initialize engine
		engine = new EVEngine(this, pokemonList, originFile);
		
		// Initialize window
		mainPanel = new JPanel(new BorderLayout());		
		
		// North Panel: Radio buttons with items & pokerus checkbox
		northPanel = new JPanel(new BorderLayout());
		pokerusCheckbox = new JCheckBox(POKERUS_NAME);
		pokerusCheckbox.setSelected(trainerPokemon.hasPokerus());
		itemPanel = new JPanel(new GridLayout(ITEM_PANEL_ROWS, ITEM_NAMES.length / ITEM_PANEL_ROWS));
		itemButtons = new JRadioButton[ITEM_NAMES.length];
		ButtonGroup itemButtonGroup = new ButtonGroup();
		for(int i = 0; i < itemButtons.length; i++)
		{
			itemButtons[i] = new JRadioButton(ITEM_NAMES[i]);
			itemButtons[i].addActionListener(engine);
			itemButtonGroup.add(itemButtons[i]);
			itemPanel.add(itemButtons[i]);
		}
		namePanel = new JPanel(new BorderLayout());
		JPanel savePanel = new JPanel(new GridLayout(1, ITEM_NAMES.length / ITEM_PANEL_ROWS));
		savePanel.add(pokerusCheckbox);
		JTextField namePretext = new JTextField(MSG_NAME);
		namePretext.setEditable(false);
		namePretext.setFont(new Font(namePretext.getFont().getName(), Font.BOLD, namePretext.getFont().getSize()));
		namePanel.add(namePretext, BorderLayout.WEST);
		nameField = new JTextField(trainerPokemon.getName());
		namePanel.add(nameField, BorderLayout.CENTER);
		savePanel.add(namePanel);
		buttonSave = new JButton(BUTTON_SAVE_TEXT);
		buttonSave.addActionListener(engine);
		savePanel.add(buttonSave);
		buttonLoad = new JButton(BUTTON_LOAD_TEXT);
		buttonLoad.addActionListener(engine);
		savePanel.add(buttonLoad);
		northPanel.add(savePanel, BorderLayout.CENTER);
		northPanel.add(itemPanel, BorderLayout.NORTH);
		mainPanel.add(northPanel, BorderLayout.NORTH);
		itemButtons[0].setSelected(true);
		
		// Central Panel: List of selected Pokemon
		centralPanel = new JPanel(new GridLayout(1, POKE_PANEL_MAX));
		pokePanels = new JPanel[POKE_PANEL_MAX];
		pokePanelComboBoxes = new JComboBox[POKE_PANEL_MAX];
		pokePanelStatYields = new JTextField[POKE_PANEL_MAX][STAT_NAMES.length];
		buttonPokeKO = new JButton[POKE_PANEL_MAX];
		for(int i = 0; i < pokePanels.length; i++)
		{
			pokePanels[i] = new JPanel(new GridLayout(4, 1));
			pokePanelComboBoxes[i] = new JComboBox(pokemonNames);
			pokePanelComboBoxes[i].addActionListener(engine);
			pokePanels[i].add(pokePanelComboBoxes[i]);
			JPanel statNameSubpanel = new JPanel(new GridLayout(1, STAT_NAMES.length));
			for(int j = 0; j < STAT_NAMES.length; j++)
			{
				JTextField statNameField = new JTextField(STAT_NAMES[j]);
				statNameField.setEditable(false);
				statNameSubpanel.add(statNameField);
			}
			pokePanels[i].add(statNameSubpanel);
			JPanel statSubpanel = new JPanel(new GridLayout(1, STAT_NAMES.length));
			for(int j = 0; j < STAT_NAMES.length; j++)
			{
				pokePanelStatYields[i][j] = new JTextField();
				pokePanelStatYields[i][j].setEditable(false);
				statSubpanel.add(pokePanelStatYields[i][j]);
			}
			pokePanels[i].add(statSubpanel);
			buttonPokeKO[i] = new JButton(BUTTON_KO_TEXT);
			buttonPokeKO[i].addActionListener(engine);
			pokePanels[i].add(buttonPokeKO[i]);
			centralPanel.add(pokePanels[i]);
		}
		mainPanel.add(centralPanel, BorderLayout.CENTER);
		
		// South Panel: EV Grid
		int numStats = STAT_NAMES.length;
		int statPanelWidth = numStats + 2; // offset of 2 for name column and button column
		statPanel = new JPanel(new GridLayout(STAT_PANEL_HEIGHT, statPanelWidth));
		
		// Add stat names
		statNames = new JTextField[statPanelWidth];
		statNames[0] = new JTextField(STAT_NAMES_PRETEXT);
		statNames[0].setEditable(false);
		statPanel.add(statNames[0]);
		for(int i = 1; i < statNames.length - 1; i++)
		{
			statNames[i] = new JTextField(STAT_NAMES[i - 1]);
			statNames[i].setEditable(false);
			statPanel.add(statNames[i]);
		}
		statNames[statNames.length - 1] = new JTextField("");
		statNames[statNames.length - 1].setEditable(false);
		statPanel.add(statNames[statNames.length - 1]);
		
		// Add stat values
		int[][] statSet = trainerPokemon.getEVSpread();
		evTotals = new JTextField[statPanelWidth - 1];
		evTotals[0] = new JTextField(STAT_VALUES_PRETEXT);
		evTotals[0].setEditable(false);
		statPanel.add(evTotals[0]);
		for(int i = 1; i < evTotals.length; i++)
		{
			evTotals[i] = new JTextField(Integer.toString(statSet[0][i - 1]));
			evTotals[i].setEditable(STAT_VALS_START_EDITABLE);
			statPanel.add(evTotals[i]);
		}
		buttonTotalLock = new JButton();
		if(STAT_VALS_START_EDITABLE)
			buttonTotalLock.setText(BUTTON_LOCK_TEXT);
		else
			buttonTotalLock.setText(BUTTON_UNLOCK_TEXT);
		buttonTotalLock.addActionListener(engine);
		statPanel.add(buttonTotalLock);
		
		// Add stat goals
		evGoals = new JTextField[statPanelWidth - 1];
		evGoals[0] = new JTextField(STAT_GOALS_PRETEXT);
		evGoals[0].setEditable(false);
		statPanel.add(evGoals[0]);
		for(int i = 1; i < evGoals.length; i++)
		{
			evGoals[i] = new JTextField(Integer.toString(statSet[1][i - 1]));
			evGoals[i].setEditable(STAT_GOALS_START_EDITABLE);
			statPanel.add(evGoals[i]);
		}
		buttonGoalLock = new JButton();
		if(STAT_GOALS_START_EDITABLE)
			buttonGoalLock.setText(BUTTON_LOCK_TEXT);
		else
			buttonGoalLock.setText(BUTTON_UNLOCK_TEXT);
		buttonGoalLock.addActionListener(engine);
		statPanel.add(buttonGoalLock);
		
		// Add to main panel
		mainPanel.add(statPanel, BorderLayout.SOUTH);
		
		// Make this all visible
		JFrame frame = new JFrame(WINDOW_NAME + trainerPokemon.getName());
		frame.setContentPane(mainPanel);
		frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		InputStream iconStream = EVWindow.class.getResourceAsStream(WINDOW_ICON);
		try
		{
			frame.setIconImage(ImageIO.read(iconStream));
		}
		catch(IOException e)
		{
			System.err.println(MSG_ERR_ICON);
		}
		catch(IllegalArgumentException e)
		{
			System.err.println(MSG_ERR_ICON);
		}
		frame.setName(WINDOW_NAME + trainerPokemon.getName());
		frame.setVisible(true);
		
		engine.updateAllPokePanels();
	}
}