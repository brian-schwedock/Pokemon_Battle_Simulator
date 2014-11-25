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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class GameApplication extends JFrame {

	/*
	 * GUI Components
	 */

	//Components for chatbox
	private JPanel chatBoxPanel;
	private StyledDocument doc;
	//private JTextArea chatTextArea;
	private JTextPane chatTextPane;
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
	int playerNumber;
	Image opposingPokemonImage;
	String opposingPokemonName;
	int opposingPokemonCurrentHP;
	int opposingPokemonMaxHP;
	int opposingPokemonAlive;

	/**
	 * the outToServer stream is a connection to the server that every client (GameApplication) uses
	 * to send ClientToServer objects to the server
	 */
	ObjectOutputStream outToServer;

	public GameApplication (ServerToClient stc, ObjectOutputStream ops) {
		super("Pokemon Battle Simulator");

		//Set all initial Pokemon information
		setPlayerNames(stc.playerNumber);
		playerNumber = stc.playerNumber;
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

		outToServer = ops;

		setVisible(true);
	}

	public void createChatBoxPanel () {
		chatBoxPanel = new JPanel ();
		chatBoxPanel.setLayout(new BoxLayout (chatBoxPanel, BoxLayout.Y_AXIS));

		chatTextPane = new JTextPane ();
		doc = chatTextPane.getStyledDocument();
		chatTextPane.setPreferredSize(new Dimension (400, 572));
		chatTextPane.setEnabled(false);
		JScrollPane jsp = new JScrollPane (chatTextPane);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		bottomChatPanel = new JPanel ();
		JLabel chatBoxPlayerLabel = new JLabel (playerName);
		messageField = new JTextField ();
		SendMessageListener sml= new SendMessageListener();
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
			if (i == currentPokemon - 1)
				pokemonSwitchButton.setEnabled(false);

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
		return allPokemon.get(number - 1).getFrontImage();
	}

	public Image getCurrentPokemonImage () {
		return allPokemon.get(0).getBackImage();
	}

	public Image getOpposingPokemonImage () {
		return opposingPokemonImage;
	}

	public String getPokemonName () {
		return allPokemon.get(0).getName();
	}

	public String getOpposingPokemonName () {
		return opposingPokemonName;
	}

	public int getCurrentHP () {
		return allPokemon.get(0).getCurrentHP();
	}

	public int getOpposingCurrentHP () {
		return opposingPokemonCurrentHP;
	}

	public int getMaxHP () {
		return allPokemon.get(0).getMaxHP();
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
		opposingPokemonAlive = alive;
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

	public void addMessage (String message, String playerName) {
		try{
			doc.insertString(doc.getLength(), playerName + ": ", null);
			String buff =message.toLowerCase();
			chatTextPane.setCaretPosition(doc.getLength());
			if(buff.contains("kappa")){
				while(true)
				{
					buff =message.toLowerCase();
					String before = message.substring(0,buff.indexOf("kappa"));
					doc.insertString(doc.getLength(), before, null);
					chatTextPane.insertIcon(new ImageIcon("./images/kappa.png"));
					message = message.substring(buff.indexOf("kappa") + 5, message.length());
					buff = message.toLowerCase();
					if(buff.length()< 5 || !buff.contains("kappa"))
						break;
				}
			}
			else if(buff.contains("keepo")){
				while(true)
				{
					buff =message.toLowerCase();
					String before = message.substring(0,buff.indexOf("keepo"));
					doc.insertString(doc.getLength(), before, null);
					chatTextPane.insertIcon(new ImageIcon("./images/keepo.png"));
					message = message.substring(buff.indexOf("keepo") + 5, message.length());
					buff = message.toLowerCase();
					if(buff.length()< 5 || !buff.contains("keepo"))
						break;
				}
			}
			else if(buff.contains("frankerz")){
				chatTextPane.setCaretPosition(doc.getLength());
				while(true)
				{
					buff =message.toLowerCase();
					String before = message.substring(0,buff.indexOf("frankerz"));
					doc.insertString(doc.getLength(), before, null);
					chatTextPane.insertIcon(new ImageIcon("./images/frankerz.png"));
					message = message.substring(buff.indexOf("frankerz") + 8, message.length());
					buff = message.toLowerCase();
					if(buff.length()< 8 || !buff.contains("frankerz"))
						break;
				}
			}
			else if(buff.contains("residentsleeper")){
				chatTextPane.setCaretPosition(doc.getLength());
					while(true)
					{
						buff =message.toLowerCase();
						String before = message.substring(0,buff.indexOf("residentsleeper"));
						doc.insertString(doc.getLength(), before, null);
						chatTextPane.insertIcon(new ImageIcon("./images/residentsleeper.png"));
						message = message.substring(buff.indexOf("residentsleeper") + 15, message.length());
						buff = message.toLowerCase();
						if(buff.length()< 15 || !buff.contains("residentsleeper"))
							break;
					}
			}
			else if(buff.contains("dududu")){
				chatTextPane.setCaretPosition(doc.getLength());
				while(true)
				{
					buff =message.toLowerCase();
					String before = message.substring(0,buff.indexOf("dududu"));
					doc.insertString(doc.getLength(), before, null);
					chatTextPane.insertIcon(new ImageIcon("./images/duDudu.png"));
					message = message.substring(buff.indexOf("dududu") + 6, message.length());
					buff = message.toLowerCase();
					if(buff.length()< 6 || !buff.contains("dududu"))
						break;
				}
			}
			else if(buff.contains("praiseit")){
				chatTextPane.setCaretPosition(doc.getLength());
				while(true)
				{
					buff =message.toLowerCase();
					String before = message.substring(0,buff.indexOf("praiseit"));
					doc.insertString(doc.getLength(), before, null);
					chatTextPane.insertIcon(new ImageIcon("./images/praiseit.png"));
					message = message.substring(buff.indexOf("praiseit") + 8, message.length());
					buff = message.toLowerCase();
					if(buff.length()< 8 || !buff.contains("praiseit"))
						break;
				}
			}
			else if(buff.contains("biblethump")){
				chatTextPane.setCaretPosition(doc.getLength());
				while(true)
				{
					buff =message.toLowerCase();
					String before = message.substring(0,buff.indexOf("biblethump"));
					doc.insertString(doc.getLength(), before, null);
					chatTextPane.insertIcon(new ImageIcon("./images/biblethump.png"));
					message = message.substring(buff.indexOf("biblethump") + 8, message.length());
					buff = message.toLowerCase();
					if(buff.length()< 10 || !buff.contains("biblethump"))
						break;
				}
			}
			doc.insertString(doc.getLength(), message + "\n", null);
		}catch(BadLocationException e){
			e.printStackTrace();
		}
		messageField.setText("");
	}

	public void resetBottomPanel () {
		//TODO: write the function
	}

	/**
     * updates the attack buttons to show the 4 moves of the current pokemon
     * in battle. This method should be called any time the player switches
     * pokemon.
     */
    public void updateAttackButtons(){
    	for (int i=0; i < 4; ++i){
			JButton tempButton = attackButtons.get(i);
			tempButton.setText(allPokemon.get(0).getMoves().get(i).getName());
			//tempButton.setPreferredSize(new Dimension (190, 30));
			//attackButton.addActionListener(al);

			String type =  allPokemon.get(0).getMoves().get(i).getType();
			int isSpecial =  allPokemon.get(0).getMoves().get(i).isSpecial();
			String specialPhysical;
			if (isSpecial == 0)
				specialPhysical = "Physical";
			else
				specialPhysical = "Special";
			int power = allPokemon.get(0).getMoves().get(i).getAttackPower();
			int accuracy = allPokemon.get(0).getMoves().get(i).getAccuracy();
			tempButton.setToolTipText(type + " - " + specialPhysical + " - Power:" + power + " - Accuracy:" + accuracy);
			
			//attackButtons.add(attackButton);
			//attackButtonPanel.add(attackButton);
		}
    }
    
    /**
     * updates the switch buttons to show the 6 pokemon in the player's party
     * This method should be called after the player switches pokemon
     */
    public void updateSwitchButtons(){
    	for (int i=0; i < 6; ++i){
			JButton tempButton = pokemonSwitchButtons.get(i);
			tempButton.setText(allPokemon.get(i).getName());
			if (i == currentPokemon - 1)
				tempButton.setEnabled(false);
			
			Image scaledImage = allPokemon.get(i).getFrontImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
			tempButton.setIcon(new ImageIcon (scaledImage));		
			
			int curHP = allPokemon.get(i).getCurrentHP();
			int maxHP = allPokemon.get(i).getMaxHP();
			String type =  allPokemon.get(i).getType();
			tempButton.setToolTipText(curHP + "/" + maxHP + " - " + type);
		}
    }

	class AttackListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			int moveChosen = 0;
			if (ae.getSource() == attackButtons.get(0)){
				System.out.println("Attack 1 clicked");
				//TODO: Remove print and send message to server
				moveChosen = 1;
			}
			else if (ae.getSource() == attackButtons.get(1)){
				System.out.println("Attack 2 clicked");
				//TODO: Remove print and send message to server
				moveChosen = 2;
			}
			else if (ae.getSource() == attackButtons.get(2)){
				System.out.println("Attack 3 clicked");
				//TODO: Remove print and send message to server
				moveChosen = 3;
			}
			else{
				System.out.println("Attack 4 clicked");
				//TODO: Remove print and send message to server
				moveChosen = 4;
			}
			System.out.println("Sending client to server class to attack");
			ClientToServer cts = new ClientToServer(3, "", moveChosen, 1);
			sendCTS(cts);
		}
	}

	/**
	 * 
	 *	This is called when the user has pressed enter and wishes to send a message
	 * 	that they typed out in the chat box.
	 * 	It adds their message to their chat field and sends their message to the 
	 *  server to be sent to the opposing player as well.
	 *
	 */
	class SendMessageListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String enteredMessage = messageField.getText();
			if(!enteredMessage.equals("")){
				addMessage (enteredMessage, playerName);
				//System.out.println("Trying to send message from client: " + playerName );
				ClientToServer cts = new ClientToServer(1, enteredMessage, 0, 0);
				sendCTS(cts);
			}
		}

	}

	class PokemonSwitchListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			int chosenPokemon;
			if (ae.getSource() == pokemonSwitchButtons.get(0)){
				System.out.println("Pokemon 1 clicked");
				//TODO: Remove print and send message to server
				chosenPokemon = 1;
			}
			else if (ae.getSource() == pokemonSwitchButtons.get(1)){
				System.out.println("Pokemon 2 clicked");
				//TODO: Remove print and send message to server
				chosenPokemon = 2;
			}
			else if (ae.getSource() == pokemonSwitchButtons.get(2)){
				System.out.println("Pokemon 3 clicked");
				chosenPokemon = 3;
				//TODO: Remove print and send message to server
			}
			else if (ae.getSource() == pokemonSwitchButtons.get(3)){
				System.out.println("Pokemon 4 clicked");
				chosenPokemon = 4;
				//TODO: Remove print and send message to server
			}
			else if (ae.getSource() == pokemonSwitchButtons.get(4)){
				System.out.println("Pokemon 5 clicked");
				chosenPokemon = 5;
				//TODO: Remove print and send message to server
			}
			else{
				System.out.println("Pokemon 6 clicked");
				chosenPokemon = 6;
				//TODO: Remove print and send message to server
			}
			System.out.println("Sending client to server class to switch pokemon");
			ClientToServer cts = new ClientToServer(3, "", 0, chosenPokemon);
			sendCTS(cts);
		}
	}



	/**
	 * Sends the cts object to the server by putting it in the
	 * object output stream
	 * @param cts is the client to server object being sent
	 */
	private void sendCTS(ClientToServer cts){
		try {
			outToServer.writeObject(cts);
			outToServer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			ObjectOutputStream outToServer = new ObjectOutputStream(startGame.getOutputStream());
			stc = (ServerToClient) inFromServer.readObject(); 

			for (Pokemon k: stc.allPokemon)
				k.setImages();
			GameApplication ga = new GameApplication (stc, outToServer);
			ClientThread ct = new ClientThread (inFromServer, ga);
			ct.start();

			//startGame.close(); 
		} catch (Exception e){ 
			System.out.println("Please run the server first"); 
		} 	

	}
}
