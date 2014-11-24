/*
 * Team members: Brian Schwedock, Ryan Chen,
 * Allen Shi, Chris Holmes, Jonathan Luu, and Alejandro Lopez
 */

package pokemon_simulator;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

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
	String playerName;
	String opposingPlayerName;
	int currentPokemon;
	Image opposingPokemonImage;
	String opposingPokemonName;
	int opposingPokemonCurrentHP;
	int opposingPokemonMaxHP;
	int opposingPokemonAlive;
	
	public GameApplication (ServerToClient stc) {
		super("Pokemon Battle Simulator");
		
		//Set all initial Pokemon information
		setPlayerNames(stc.playerNumber);
		setAllPokemon(stc.allPokemon);
		setCurrentPokemon(stc.pokemonInPlay);
		setOpposingPokemonImage(stc.opposingPokemonImage);
		setOpposingPokemonCurrentHP(stc.opposingCurrentHP);
		setOpposingPokemonMaxHP(stc.opposingMaxHP);
		setOpposingPokemonAlive(stc.opposingPokemonAlive);
		setOpposingPokemonName(stc.opposingPokemonName);
		
		
		//GUI Initializations
		setSize(1200, 650);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout ());
		
		createChatBoxPanel();
		createGameScreenPanel();
		
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
		JLabel chatBoxPlayerLabel = new JLabel (playerName);
		messageField = new JTextField ();
		sendMessageListener sml= new sendMessageListener();
		messageField.addActionListener(sml);
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
		animationPanel = new AnimationPanel (this, playerName, opposingPlayerName);
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
		
		//Create attack buttons
		JPanel attackButtonPanel = new JPanel ();
		attackButtons = new ArrayList<JButton> ();
		AttackListener al = new AttackListener ();
		for (int i=0; i < 4; ++i){
			JButton attackButton = new JButton (allPokemon.get(currentPokemon - 1).getMoves().get(i).getName());
			attackButton.setPreferredSize(new Dimension (190, 30));
			attackButton.addActionListener(al);

			String type =  allPokemon.get(currentPokemon - 1).getMoves().get(i).getType();
			int isSpecial =  allPokemon.get(currentPokemon - 1).getMoves().get(i).isSpecial();
			String specialPhysical;
			if (isSpecial == 0)
				specialPhysical = "Physical";
			else
				specialPhysical = "Special";
			int power = allPokemon.get(currentPokemon - 1).getMoves().get(i).getAttackPower();
			int accuracy = allPokemon.get(currentPokemon - 1).getMoves().get(i).getAccuracy();
			attackButton.setToolTipText(type + " - " + specialPhysical + " - Power:" + power + " - Accuracy:" + accuracy);

			attackButtons.add(attackButton);
			attackButtonPanel.add(attackButton);
		}
		actionPanel.add(attackButtonPanel);
		
		JLabel pokemonSwitchLabel = new JLabel ("Switch");
		alignPanel = new JPanel ();
		alignPanel.setLayout (new BorderLayout());
		alignPanel.add(pokemonSwitchLabel, BorderLayout.WEST);
		actionPanel.add(alignPanel);
		
		//Create Pokemon switch buttons
		JPanel pokemonSwitchButtonPanel = new JPanel ();
		pokemonSwitchButtons = new ArrayList<JButton>();
		PokemonSwitchListener psl = new PokemonSwitchListener ();
		for (int i=0; i < 6; ++i){
			JButton pokemonSwitchButton = new JButton (allPokemon.get(i).getName());
			
			Image scaledImage = allPokemon.get(i).getFrontImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
			pokemonSwitchButton.setIcon(new ImageIcon (scaledImage));		
			
			int curHP = allPokemon.get(i).getCurrentHP();
			int maxHP = allPokemon.get(i).getMaxHP();
			String type =  allPokemon.get(i).getType();
			pokemonSwitchButton.setToolTipText(curHP + "/" + maxHP + " - " + type);
			
			pokemonSwitchButton.setPreferredSize(new Dimension (125, 30));
			pokemonSwitchButton.addActionListener(psl);
			pokemonSwitchButtons.add(pokemonSwitchButton);
			pokemonSwitchButtonPanel.add(pokemonSwitchButton);
		}
		actionPanel.add(pokemonSwitchButtonPanel);
		
		gameScreenPanel.add(actionPanel, BorderLayout.SOUTH);
	}
	
	public String getPlayerName () {
		return playerName;
	}
	
	public String getOpposingPlayerName () {
		return opposingPlayerName;
	}

	//We may not need this method
    public Image getPokemonImage (int number) {
    	return allPokemon.get(currentPokemon - 1).getFrontImage();
    }

    public Image getCurrentPokemonImage () {
    	return allPokemon.get(currentPokemon - 1).getBackImage();
    }

    public Image getOpposingPokemonImage () {
    	return opposingPokemonImage;
    }

    public String getPokemonName () {
    	return allPokemon.get(currentPokemon - 1).getName();
    }

    public String getOpposingPokemonName () {
    	return opposingPokemonName;
    }

    public int getCurrentHP () {
    	return allPokemon.get(currentPokemon - 1).getCurrentHP();
    }

    public int getOpposingCurrentHP () {
    	return opposingPokemonCurrentHP;
    }

    public int getMaxHP () {
    	return allPokemon.get(currentPokemon - 1).getMaxHP();
    }

    public int getOpposingMaxHP () {
    	return opposingPokemonMaxHP;
    }

    public void setAllPokemon (ArrayList<Pokemon> allPokemon) {
    	this.allPokemon = allPokemon;
    }

    public void setCurrentPokemon (int current) {
    	currentPokemon = current;
    	//TODO: change the GUI to reflect current Pokemon
    }

    public void setOpposingPokemonImage (String opposingPokemonString) {
    	Image opposingPokemon = new ImageIcon(opposingPokemonString).getImage();
    	this.opposingPokemonImage = opposingPokemon;
    }

    public void setOpposingPokemonCurrentHP (int currentHP) {
    	this.opposingPokemonCurrentHP = currentHP;
    }

    public void setOpposingPokemonMaxHP (int maxHP) {
    	this.opposingPokemonMaxHP = maxHP;
    }

    public void setOpposingPokemonAlive (int alive) {
    	//TODO: write the function
    }
    
    public void setOpposingPokemonName (String name) {
    	opposingPokemonName = name;
    }
    
    public void setPlayerNames (int playerNumber) {
    	if (playerNumber == 1) {
    		playerName = "Player 1";
    		opposingPlayerName = "Player 2";
    	}
    	else {
    		playerName = "Player 2";
    		opposingPlayerName = "Player 1";
    	}
    }

    public void addMessage (String message) {
    	//TODO: write the function change what the gui looks like on yours
    }

    public void resetBottomPanel () {
    	//TODO: write the function
    }

    class AttackListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == attackButtons.get(0)){
				System.out.println("Attack 1 clicked");
				//TODO: Remove print and send message to server
			}
			else if (ae.getSource() == attackButtons.get(1)){
				System.out.println("Attack 2 clicked");
				//TODO: Remove print and send message to server
			}
			else if (ae.getSource() == attackButtons.get(2)){
				System.out.println("Attack 3 clicked");
				//TODO: Remove print and send message to server
			}
			else{
				System.out.println("Attack 4 clicked");
				//TODO: Remove print and send message to server
			}
		}
    }
    class sendMessageListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String enteredMessage=messageField.getText();
			if(!enteredMessage.equals("")){
				chatTextArea.append(playerName+": " + enteredMessage);
				chatTextArea.append("\n");
				messageField.setText("");
			}
		}
    	
    }
    class PokemonSwitchListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == pokemonSwitchButtons.get(0)){
				System.out.println("Pokemon 1 clicked");
				//TODO: Remove print and send message to server
			}
			else if (ae.getSource() == pokemonSwitchButtons.get(1)){
				System.out.println("Pokemon 2 clicked");
				//TODO: Remove print and send message to server
			}
			else if (ae.getSource() == pokemonSwitchButtons.get(2)){
				System.out.println("Pokemon 3 clicked");
				//TODO: Remove print and send message to server
			}
			else if (ae.getSource() == pokemonSwitchButtons.get(3)){
				System.out.println("Pokemon 4 clicked");
				//TODO: Remove print and send message to server
			}
			else if (ae.getSource() == pokemonSwitchButtons.get(4)){
				System.out.println("Pokemon 5 clicked");
				//TODO: Remove print and send message to server
			}
			else{
				System.out.println("Pokemon 6 clicked");
				//TODO: Remove print and send message to server
			}
		}
    }
    
    /*
     * Add a key listener so that when the enter key is clicked
     * if there is a message in the textbox, the message shows up
     * in the textarea and the message is sent to the server
     */
	
	public static void main (String [] args) {
		try { 
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); } 
	    catch(Exception e){}
		
		//Connecting to the server
		Socket startGame = null;
		ServerToClient stc = null;

		try { 
			startGame = new Socket("127.0.0.1", 9000); 
 			ObjectInputStream inFromServer = new ObjectInputStream(startGame.getInputStream()); 
 			stc = (ServerToClient) inFromServer.readObject(); 
 			
 			for (Pokemon k: stc.allPokemon)
 				k.setImages();
 			new GameApplication (stc);
 			
			//startGame.close(); 
 		} catch (Exception e){ 
 			System.out.println("Please run the server first"); 
		} 	
		
	}
}
