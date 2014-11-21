/*
 * Team members: Brian Schwedock, Ryan Chen,
 * Allen Shi, Chris Holmes, and Alejandro Lopez
 */

package pokemon_simulator;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

public class GameApplication extends JFrame {
	
	/*
	 * GUI Components
	 */
	
	//Components for chatbox
	private JPanel chatBoxPanel;
	private JTextArea chatTextArea;
	private JPanel bottomChatPanel;
	private JLabel chatBoxPlayerLabel;
	private JTextField messageField;
	
	//Container for all non-chat elements
	private JPanel gameScreenPanel;
	
	//Animation panel
	private AnimationPanel animationPanel;
	
	//Components for the bottom of the gameScreenPanel
	private JPanel bottomGameScreenPanel;
	private JPanel actionPanel;
	private JPanel waitingPanel;
	private JLabel waitingLabel;
	private ArrayList<JButton> attackButtons;
	private ArrayList<JButton> pokemonSwitchButtons;
	
	
	/*
	 * Variables for gameplay
	 */
	
	ArrayList<Pokemon> allPokemon;
	int currentPokemon;
	Image opposingPokemonImage;
	String opposingPokemonName;
	int opposingPokemonCurrentHP;
	int opposingPokemonMaxHP;
	int opposingPokemonAlive;
	
	public GameApplication () {
		super("Pokemon Battle Simulator");
		setSize(1200, 650);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout ());
		
		createChatBoxPanel();
		createGameScreenPanel();
		
		
		
		//This is test code to make sure the GUI works
		Pokemon p1 = new Pokemon ("Mewtwo", "Psychic", 345, 100, 150, 400, 250, 375);
		Pokemon p2 = new Pokemon ("Alakazam", "Psychic", 345, 100, 150, 400, 250, 375);
		Pokemon p3 = new Pokemon ("Bulbasaur", "Grass", 345, 100, 150, 400, 250, 375);
		Pokemon p4 = new Pokemon ("Gengar", "Ghost", 345, 100, 150, 400, 250, 375);
		Pokemon p5 = new Pokemon ("Porygon", "Normal", 345, 100, 150, 400, 250, 375);
		Pokemon p6 = new Pokemon ("Machamp", "Fighting", 345, 100, 150, 400, 250, 375);
		ArrayList<Pokemon> allPokemon = new ArrayList<Pokemon> ();
		allPokemon.add (p1);
		allPokemon.add (p2);
		allPokemon.add (p3);
		allPokemon.add (p4);
		allPokemon.add (p5);
		allPokemon.add (p6);
		ServerToClient stc = new ServerToClient (1, 1, allPokemon, 1, (new ImageIcon ("images/frontSprites/Pikachu.gif")).getImage(), 
				350, 350, 6, "message", 100);
		
		
		
		setVisible(true);
	}
	
	public void createChatBoxPanel () {
		chatBoxPanel = new JPanel ();
		chatBoxPanel.setLayout(new BoxLayout (chatBoxPanel, BoxLayout.Y_AXIS));
		
		chatTextArea = new JTextArea ();
		chatTextArea.setPreferredSize(new Dimension (400, 572));
		chatTextArea.setEnabled(false);
		JScrollPane jsp = new JScrollPane (chatTextArea);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		bottomChatPanel = new JPanel ();
		JLabel chatBoxPlayerLabel = new JLabel ("Player 1");
		JTextField messageField = new JTextField ();
		messageField.setPreferredSize(new Dimension (350, 30));
		
		bottomChatPanel.add(chatBoxPlayerLabel);
		bottomChatPanel.add(messageField);
		
		chatBoxPanel.add(jsp);
		chatBoxPanel.add(bottomChatPanel);
		
		add(chatBoxPanel, BorderLayout.EAST);
	}
	
	public void createGameScreenPanel () {
		gameScreenPanel = new JPanel ();
		gameScreenPanel.setLayout(new BorderLayout());
		createAnimationPanel ();
		createBottomGameScreenPanel ();
		add(gameScreenPanel, BorderLayout.CENTER);
	}
	
	public void createAnimationPanel () {
		animationPanel = new AnimationPanel (this, null, null);
		gameScreenPanel.add(animationPanel, BorderLayout.CENTER);
	}
	
	public void createBottomGameScreenPanel () {
		bottomGameScreenPanel = new JPanel ();
		bottomGameScreenPanel.setLayout(new CardLayout());
		
		actionPanel = new JPanel ();
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
		
		JLabel attackLabel = new JLabel ("Attack");
		//Must create borderlayout panel to align attackLabel to the left
		JPanel alignPanel = new JPanel ();
		alignPanel.setLayout (new BorderLayout());
		alignPanel.add(attackLabel, BorderLayout.WEST);
		actionPanel.add(alignPanel);
		
		JPanel attackButtonPanel = new JPanel ();
		attackButtons = new ArrayList<JButton>();
		for (int i=1; i <= 4; ++i){
			JButton attackButton = new JButton ("Attack " + i);
			attackButton.setPreferredSize(new Dimension (190, 30));
			attackButtons.add(attackButton);
			attackButtonPanel.add(attackButton);
		}
		actionPanel.add(attackButtonPanel);
		
		JLabel pokemonSwitchLabel = new JLabel ("Switch");
		alignPanel = new JPanel ();
		alignPanel.setLayout (new BorderLayout());
		alignPanel.add(pokemonSwitchLabel, BorderLayout.WEST);
		actionPanel.add(alignPanel);
		
		JPanel pokemonSwitchButtonPanel = new JPanel ();
		pokemonSwitchButtons = new ArrayList<JButton>();
		for (int i=1; i <= 6; ++i){
			JButton pokemonSwitchButton = new JButton ("Pokemon " + i);
			pokemonSwitchButton.setPreferredSize(new Dimension (125, 30));
			pokemonSwitchButtons.add(pokemonSwitchButton);
			pokemonSwitchButtonPanel.add(pokemonSwitchButton);
		}
		actionPanel.add(pokemonSwitchButtonPanel);
		
		gameScreenPanel.add(actionPanel, BorderLayout.SOUTH);
	}
	
	public String getPlayerName () {
		//TODO: write the function
		return null;
	}

    public ImageIcon getPokemonImage (int number) {
    	//TODO: write the function
    	return null;
    }

    public ImageIcon getCurrentPokemonImage () {
    	//TODO: write the function
    	return null;
    }

    public ImageIcon getOpposingPokemonImage () {
    	//TODO: write the function
    	return null;
    }

    public String getPokemonName () {
    	//TODO: write the function
    	return null;
    }

    public String getOpposingPokemonName () {
    	//TODO: write the function
    	return null;
    }

    public int getCurrentHP () {
    	//TODO: write the function
    	return 0;
    }

    public int getOpposingCurrentHP () {
    	//TODO: write the function
    	return 0;
    }

    public int getMaxHP () {
    	//TODO: write the function
    	return 0;
    }

    public int getOpposingMaxHP () {
    	//TODO: write the function
    	return 0;
    }

    public void setAllPokemon (ArrayList<Pokemon> allPokemon) {
    	//TODO: write the function
    }

    public void setCurrentPokemon (int current) {
    	//TODO: write the function
    }

    public void setOpposingPokemonImage (ImageIcon opposingPokemonImage) {
    	//TODO: write the function
    }

    public void setOpposingPokemonCurrentHP (int currentHP) {
    	//TODO: write the function
    }

    public void setOpposingPokemonMaxHP (int maxHP) {
    	//TODO: write the function
    }

    public void setOpposingPokemonAlive (int alive) {
    	//TODO: write the function
    }

    public void addMessage (String message) {
    	//TODO: write the function
    }

    public void resetBottomPanel () {
    	//TODO: write the function
    }

	
	public static void main (String [] args) {
		
		//First, try to connect to the server
		//Only after connecting to the server,
		//create the GUI
		
		new GameApplication ();
	}
}
