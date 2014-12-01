/*
 * Team members: Brian Schwedock, Ryan Chen,
 * Allen Shi, Chris Holmes, Jonathan Luu, and Alejandro Lopez
 */

/**
 * GameApplication is the main hub for the client.
 * It contains all GUI components and interacts with Server and ClientThread.
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
import java.util.Random;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class GameApplication extends JFrame {

	/**
	 * GUI Components
	 */

	//Components for chatbox
	private JPanel chatBoxPanel;
	private StyledDocument doc;
	private SimpleAttributeSet plainAttribute;
	private SimpleAttributeSet boldAttribute;
	
	//private JTextArea chatTextArea;
	private JTextPane chatTextPane;
	private JPanel bottomChatPanel;

	private JTextField messageField;

	//Container for all non-chat elements
	private JPanel gameScreenPanel;

	//Animation panel which contains all the graphics
	private AnimationPanel animationPanel;

	//Components for the bottom of the gameScreenPanel
	/**
	 * bottomGameScreenPanel consists of AnimationPanel 
	 * and the lower panel with the buttons
	 */
	private JPanel bottomGameScreenPanel;
	/**
	 * actionPanel is the main panel which contains
	 * buttons for attacks and for switching Pokemon
	 */
	private JPanel actionPanel;
	/**
	 * waitingPanelMove appears when you have chosen
	 * a move and are waiting for your opponent
	 */
	private JPanel waitingPanelMove;
	/**
	 * waitingPanelSwitch appears when you are waiting
	 * for your opponent to choose a Pokemon after
	 * his previous Pokemon fainted
	 */
	private JPanel waitingPanelSwitch;
	/**
	 * faintedPokemonPanel appears when you have to 
	 * choose a new Pokemon after your previous
	 * Pokemon has fainted
	 */
	private JPanel faintedPokemonPanel;
	private JLabel waitingLabelMove;
	private JLabel waitingLabelSwitch;
	private JLabel faintedPokemonLabel;
	private ArrayList<JButton> attackButtons;
	private ArrayList<JButton> pokemonSwitchButtons;
	private ArrayList<JButton> faintedPokemonSwitchButtons;
	private JButton waitingCancel;


	/**
	 * Variables for gameplay
	 */

	ArrayList<Pokemon> allPokemon;
	String playerName;
	String opposingPlayerName;
	private final int currentPokemon = 0;
	int playerNumber;
	Image opposingPokemonImage;
	String opposingPokemonName;
	int opposingPokemonCurrentHP;
	int opposingPokemonMaxHP;
	int opposingPokemonAlive;
	
	private String [] emojis = {"biblethump", "dududu", "failfish", "frankerz", "kappa", 
			"keepo", "praiseit", "residentsleeper", "rip", "pjsalt", "rektbox"};

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
		
		// chat font initializations
		plainAttribute = new SimpleAttributeSet();
		plainAttribute.addAttribute(StyleConstants.FontSize, 12);
		
		boldAttribute = new SimpleAttributeSet();
		boldAttribute.addAttribute(StyleConstants.Alignment, StyleConstants.ALIGN_CENTER);
		boldAttribute.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
		boldAttribute.addAttribute(StyleConstants.FontSize, 12);

		outToServer = ops;

		setVisible(true);
	}

	public void createChatBoxPanel () {
		chatBoxPanel = new JPanel ();
		chatBoxPanel.setLayout(new BoxLayout (chatBoxPanel, BoxLayout.Y_AXIS));

		chatTextPane = new JTextPane ();
		chatTextPane.setEditorKit(new WrapEditorKit());
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
			JButton attackButton = new JButton (allPokemon.get(currentPokemon).getMoves().get(i).getName());
			attackButton.setPreferredSize(new Dimension (190, 30));
			attackButton.addActionListener(al);

			String type =  allPokemon.get(currentPokemon).getMoves().get(i).getType();
			int isSpecial =  allPokemon.get(currentPokemon).getMoves().get(i).isSpecial();
			String specialPhysical;
			if (isSpecial == 0)
				specialPhysical = "Physical";
			else
				specialPhysical = "Special";
			int power = allPokemon.get(currentPokemon).getMoves().get(i).getAttackPower();
			int accuracy = allPokemon.get(currentPokemon).getMoves().get(i).getAccuracy();
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
			if (i == currentPokemon)
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
		
		bottomGameScreenPanel.add(actionPanel, "actionPanel");
		
		/**
		 * WaitingPanelSwitch for when opponent is choosing a new Pokemon
		 */
		waitingPanelSwitch = new JPanel ();
		waitingLabelSwitch = new JLabel ("Waiting for opponent to switch Pokemon.");
		waitingPanelSwitch.add(waitingLabelSwitch);
		
		bottomGameScreenPanel.add(waitingPanelSwitch, "waitingPanelSwitch");
		
		/**
		 * waitingPanelMove for when you are waiting for opponent
		 * to make a move
		 */
		waitingPanelMove = new JPanel();
		waitingCancel = new JButton("Cancel");
		waitingLabelMove = new JLabel("Waiting for opponent to make a move.");
		waitingCancel.addActionListener(new WaitingCancelListener ());
		waitingPanelMove.add(waitingLabelMove);
		waitingPanelMove.add(waitingCancel);
		
		bottomGameScreenPanel.add(waitingPanelMove, "waitingPanelMove");
		
		/**
		 * FaintedPokemonPanel for when a new Pokemon must be selected
		 */
		faintedPokemonPanel = new JPanel ();
		faintedPokemonPanel.setLayout(new BoxLayout(faintedPokemonPanel, BoxLayout.Y_AXIS));
		
		faintedPokemonLabel = new JLabel ("Switch to:");
		alignPanel = new JPanel ();
		alignPanel.setLayout (new BorderLayout());
		alignPanel.add(faintedPokemonLabel, BorderLayout.WEST);
		faintedPokemonPanel.add(alignPanel);
		
		//Create fainted Pokemon switch buttons
		JPanel faintedPokemonSwitchButtonPanel = new JPanel ();
		faintedPokemonSwitchButtons = new ArrayList<JButton>();
		FaintedPokemonSwitchListener fpsl = new FaintedPokemonSwitchListener ();
		for (int i=0; i < 6; ++i){
			JButton faintedPokemonSwitchButton = new JButton (allPokemon.get(i).getName());
			if (i == currentPokemon)
				faintedPokemonSwitchButton.setEnabled(false);

			Image scaledImage = allPokemon.get(i).getFrontImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
			faintedPokemonSwitchButton.setIcon(new ImageIcon (scaledImage));		

			int curHP = allPokemon.get(i).getCurrentHP();
			int maxHP = allPokemon.get(i).getMaxHP();
			String type =  allPokemon.get(i).getType();
			faintedPokemonSwitchButton.setToolTipText(curHP + "/" + maxHP + " - " + type);

			faintedPokemonSwitchButton.setPreferredSize(new Dimension (125, 30));
			faintedPokemonSwitchButton.addActionListener(fpsl);
			faintedPokemonSwitchButtons.add(faintedPokemonSwitchButton);
			faintedPokemonSwitchButtonPanel.add(faintedPokemonSwitchButton);
		}
		faintedPokemonPanel.add(faintedPokemonSwitchButtonPanel);
		
		faintedPokemonPanel.add(Box.createRigidArea(new Dimension(0, 60)));
		
		bottomGameScreenPanel.add(faintedPokemonPanel, "faintedPokemonPanel");

		gameScreenPanel.add(bottomGameScreenPanel, BorderLayout.SOUTH);
	}

	public String getPlayerName () {
		return playerName;
	}

	public String getOpposingPlayerName () {
		return opposingPlayerName;
	}
	
	public Image getCurrentPokemonImage () {
		return allPokemon.get(currentPokemon).getBackImage();
	}

	public Image getOpposingPokemonImage () {
		return opposingPokemonImage;
	}

	public String getPokemonName () {
		return allPokemon.get(currentPokemon).getName();
	}

	public String getOpposingPokemonName () {
		return opposingPokemonName;
	}

	public int getCurrentHP () {
		return allPokemon.get(currentPokemon).getCurrentHP();
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
		for (int i = 0; i < 6; ++i){
			allPokemon.get(i).setImages();
		}
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
	
	public void crossoutFaintedPokemon(){
		animationPanel.crossOutPokemon();
	}
	
	public void crossoutOpposingFaintedPokemon(){
		animationPanel.crossOutOpposingPokemon();
	}
	
	public synchronized void addMessage (String message) {
		try {
			doc.insertString(doc.getLength(), message, null);
			doc.insertString(doc.getLength(), "\n", null);
			chatTextPane.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			//e.printStackTrace();
		}
	}

	public synchronized void addChatMessage(String message, String playerName){
		try{
			chatTextPane.setCaretPosition(doc.getLength());
			doc.insertString(doc.getLength(), playerName  + ": ", boldAttribute);
			String [] words = message.split(" ");
			int position = 0; 
			for(int i = 0; i < words.length; i++){
				boolean emojiFound = false;
				int indexOfWord = message.indexOf(words[i], position);
				for(int j = 0; j < emojis.length; j++){
					if(words[i].equalsIgnoreCase(emojis[j])){
						// found an emoji inside the string message
						emojiFound = true;
						// print out any trailing whitespace the user may have put in
						doc.insertString(doc.getLength(), message.substring(position, indexOfWord), plainAttribute);
						// now print out the emoji
						chatTextPane.setCaretPosition(doc.getLength());
						chatTextPane.insertIcon(new ImageIcon("./images/" + emojis[j] + ".png"));
						chatTextPane.setCaretPosition(doc.getLength());
						position = indexOfWord + emojis[j].length();
					}
				}
				if(!emojiFound){
					doc.insertString(doc.getLength(), message.substring(position, indexOfWord +words[i].length()), plainAttribute);
					position = indexOfWord + words[i].length();
				}
			}
			doc.insertString(doc.getLength(), "\n",plainAttribute);
		}catch(BadLocationException e){
			//e.printStackTrace();
		}
	}

	public void resetBottomPanel () {
		updateAttackButtons();
		updateSwitchButtons();
	}

	/**
     * updates the attack buttons to show the 4 moves of the current Pokemon
     * in battle. This method should be called any time the player switches
     * Pokemon.
     */
    private void updateAttackButtons(){
    	for (int i=0; i < 4; ++i){
			attackButtons.get(i).setText(allPokemon.get(currentPokemon).getMoves().get(i).getName());

			String type =  allPokemon.get(currentPokemon).getMoves().get(i).getType();
			int isSpecial =  allPokemon.get(currentPokemon).getMoves().get(i).isSpecial();
			String specialPhysical;
			if (isSpecial == 0)
				specialPhysical = "Physical";
			else
				specialPhysical = "Special";
			int power = allPokemon.get(currentPokemon).getMoves().get(i).getAttackPower();
			int accuracy = allPokemon.get(currentPokemon).getMoves().get(i).getAccuracy();
			attackButtons.get(i).setToolTipText(type + " - " + specialPhysical + " - Power:" + power + " - Accuracy:" + accuracy);
		}
    }
    
    /**
     * updates the switch buttons to show the 6 Pokemon in the player's party
     * This method should be called after the player switches Pokemon
     */
    private void updateSwitchButtons(){
    	for (int i=0; i < 6; ++i){
    		//Update switchButtons for actionPanel
			pokemonSwitchButtons.get(i).setText(allPokemon.get(i).getName());
			
			Image scaledImage = allPokemon.get(i).getFrontImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
			pokemonSwitchButtons.get(i).setIcon(new ImageIcon (scaledImage));		
			
			int curHP = allPokemon.get(i).getCurrentHP();
			int maxHP = allPokemon.get(i).getMaxHP();
			String type =  allPokemon.get(i).getType();
			pokemonSwitchButtons.get(i).setToolTipText(curHP + "/" + maxHP + " - " + type);
			
			if (i == 0)
				pokemonSwitchButtons.get(i).setEnabled(false);
			else if (allPokemon.get(i).isFainted())
				pokemonSwitchButtons.get(i).setEnabled(false);
			else
				pokemonSwitchButtons.get(i).setEnabled(true);
			
			//Update switchButtons for faintedPokemonPanel
			faintedPokemonSwitchButtons.get(i).setText(allPokemon.get(i).getName());
			faintedPokemonSwitchButtons.get(i).setIcon(new ImageIcon (scaledImage));		
			faintedPokemonSwitchButtons.get(i).setToolTipText(curHP + "/" + maxHP + " - " + type);
			
			if (allPokemon.get(i).isFainted())
				faintedPokemonSwitchButtons.get(i).setEnabled(false);
			else
				faintedPokemonSwitchButtons.get(i).setEnabled(true);
		}
    }

    class WaitingCancelListener implements ActionListener {
    	public void actionPerformed(ActionEvent ae) {
    		changeBottomPanel(1);
    		
    		ClientToServer cts = new ClientToServer(5, "", 0, 0);
			sendCTS(cts);
    	}
    }
    
	class AttackListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			int moveChosen = 0;
			if (ae.getSource() == attackButtons.get(0))
				moveChosen = 1;
			else if (ae.getSource() == attackButtons.get(1))
				moveChosen = 2;
			else if (ae.getSource() == attackButtons.get(2))
				moveChosen = 3;			
			else
				moveChosen = 4;

			ClientToServer cts = new ClientToServer(2, "", moveChosen, 1);
			sendCTS(cts);
			
			changeBottomPanel (4);
		}
	}
	
	class PokemonSwitchListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			int chosenPokemon;
			if (ae.getSource() == pokemonSwitchButtons.get(0))
				chosenPokemon = 1;
			else if (ae.getSource() == pokemonSwitchButtons.get(1))
				chosenPokemon = 2;
			else if (ae.getSource() == pokemonSwitchButtons.get(2))
				chosenPokemon = 3;
			else if (ae.getSource() == pokemonSwitchButtons.get(3))
				chosenPokemon = 4;
			else if (ae.getSource() == pokemonSwitchButtons.get(4))
				chosenPokemon = 5;
			else
				chosenPokemon = 6;

			ClientToServer cts = new ClientToServer(3, "", 0, chosenPokemon);
			sendCTS(cts);
			
			changeBottomPanel (4);
		}
	}
	
	class FaintedPokemonSwitchListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			int chosenPokemon;
			if (ae.getSource() == faintedPokemonSwitchButtons.get(0))
				chosenPokemon = 1;
			else if (ae.getSource() == faintedPokemonSwitchButtons.get(1))
				chosenPokemon = 2;
			else if (ae.getSource() == faintedPokemonSwitchButtons.get(2))
				chosenPokemon = 3;
			else if (ae.getSource() == faintedPokemonSwitchButtons.get(3))
				chosenPokemon = 4;
			else if (ae.getSource() == faintedPokemonSwitchButtons.get(4))
				chosenPokemon = 5;
			else
				chosenPokemon = 6;

			ClientToServer cts = new ClientToServer(4, "", 0, chosenPokemon);
			sendCTS(cts);
			
			changeBottomPanel (2);
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
			if(!enteredMessage.equals("")) {
				addChatMessage (enteredMessage, playerName);
				messageField.setText("");
				ClientToServer cts = new ClientToServer(1, enteredMessage, 0, 0);
				sendCTS(cts);
			}
		}
	}


	/**
	 * Sends the cts object to the server by putting it in the
	 * object output stream
	 * @param cts is the client to server object being sent
	 */
	private void sendCTS(ClientToServer cts) {
		try {
			outToServer.writeObject(cts);
			outToServer.flush();
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}

	public void changeBottomPanel (int action) {
		//1 changes to actionPanel
		//2 changes to waitingPanelSwitch
		//3 changes to faintedPokemonPanel
		//4 changes to waitingPanelMove
		
		CardLayout cl = (CardLayout) bottomGameScreenPanel.getLayout();
		if (action == 1)
			cl.show(bottomGameScreenPanel, "actionPanel");
		else if (action == 2)
			cl.show(bottomGameScreenPanel, "waitingPanelSwitch");
		else if (action == 3)
			cl.show(bottomGameScreenPanel, "faintedPokemonPanel");
		else			
			cl.show(bottomGameScreenPanel, "waitingPanelMove");
	}
	
	public void enableCancel (boolean enable) {
		waitingCancel.setEnabled(enable);
	}
	
	public void won(boolean playerWon) {
		//You won!
		
		int opposingPlayerNumber;
		if (playerNumber == 1)
			opposingPlayerNumber = 2;
		else
			opposingPlayerNumber = 1;
		
		if(playerWon) {
			Random r = new Random();
			
			JOptionPane.showMessageDialog (this,
					"You received $" + (r.nextInt(1001) + 1) + " for winning!" ,
					"You defeated Player " + opposingPlayerNumber + "!",
					JOptionPane.INFORMATION_MESSAGE);

			System.exit(0);
		}
		else {  //You Lost
			
			JOptionPane.showMessageDialog (this,
					"Player " + opposingPlayerNumber + " took your money!" ,
					"You lost to Player " + opposingPlayerNumber + "!",
					JOptionPane.WARNING_MESSAGE);
			
			System.exit(0);
		}
	}
	
	class WrapEditorKit extends StyledEditorKit {
	    ViewFactory defaultFactory=new WrapColumnFactory();
	    public ViewFactory getViewFactory() {
	        return defaultFactory;
	    }
	}

	class WrapColumnFactory implements ViewFactory {
	    public View create(Element elem) {
	        String kind = elem.getName();
	        if (kind != null) {
	            if (kind.equals(AbstractDocument.ContentElementName)) {
	                return new WrapLabelView(elem);
	            } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
	                return new ParagraphView(elem);
	            } else if (kind.equals(AbstractDocument.SectionElementName)) {
	                return new BoxView(elem, View.Y_AXIS);
	            } else if (kind.equals(StyleConstants.ComponentElementName)) {
	                return new ComponentView(elem);
	            } else if (kind.equals(StyleConstants.IconElementName)) {
	                return new IconView(elem);
	            }
	        }
	
	        // default to text display
	        return new LabelView(elem);
	    }
	}
	
	class WrapLabelView extends LabelView {
	    public WrapLabelView(Element elem) {
	        super(elem);
	    }
	
	    public float getMinimumSpan(int axis) {
	        switch (axis) {
	            case View.X_AXIS:
	                return 0;
	            case View.Y_AXIS:
	                return super.getMinimumSpan(axis);
	            default:
	                throw new IllegalArgumentException("Invalid axis: " + axis);
	        }
	    }
	}

	public static void main (String [] args) {
		try { 
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); } 
		catch(Exception e){}

		//Connecting to the server
		Socket startGame = null;
		ServerToClient stc = null;
		//clip is for music
		LoopSound clip = new LoopSound();
		try { 
			Scanner sc = new Scanner (System.in);
			System.out.print("Enter IP address of server: ");
			String ipAddress = sc.next();
			sc.close();
			startGame = new Socket(ipAddress, 9000); 
			ObjectInputStream inFromServer = new ObjectInputStream(startGame.getInputStream());
			ObjectOutputStream outToServer = new ObjectOutputStream(startGame.getOutputStream());
			stc = (ServerToClient) inFromServer.readObject(); 

			for (Pokemon k: stc.allPokemon)
				k.setImages();
			GameApplication ga = new GameApplication (stc, outToServer);
			ClientThread ct = new ClientThread (inFromServer, ga);
			ct.start();
			Thread t = new Thread(clip);
			t.start();
		} catch (Exception e){ 
			System.out.println("Please run the server first"); 
		} 	
	}
}
