package pokemon_simulator;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * The intermediary that sends, receives, and manipulates data 
 * from the players and chat box. The Server class will perform many 
 * of the important logic and mathematical calculations as well as 
 * store much of the important game state data. Therefore, we can use 
 * database and server interchangeably
 * <p>
 * Assumptions: There is already a database of the appropriate data 
 * needed in same project directory that the server class resides in. 
 * For example, a comma-delimited file containing a list of all the 
 * pokemon and or their moves/stats in order.
 * <p>
 * General Format: The Server class will be a separate class from the 
 * two player threads and the chatBox thread. The main method for 
 * transferring data will be through serializable classes containing 
 * necessary information about both players. These two classes are 
 * named ServerToClient and ClientToServer.
 * 
 *
 */
public class Server {
	static Map<String, Move> allMoves = new HashMap<String,Move>();
	static Map<Integer, Pokemon> allPokemon = new HashMap<Integer, Pokemon>();
		
	
	private static Player playerOne;
	private static Player playerTwo;
	
	// true if playerOne wins the game false otherwise
	private boolean playerOneVictory;
	// true if playerTwo wins the game false otherwise
	private boolean playerTwoVictory;
	// true if the game is still going on meaning neither player has won
	private boolean gameOn;
	
	/**
	 * array list containing all of the ServerThreads created
	 * (the number of connections to the server socket)
	 */
	private ArrayList<ServerThread> serverThreads;
	
	/**
	 * keeps track of the player actions during every turn. 
	 * A chat message is not a player action. A player Action is 
	 * either a move(attack) or change of pokemon. A turn is not 
	 * completed until both players have completed an action 
	 * (2 actions)
	 */
	private int actionCount;
	
	
	Server(){
		try {
			ServerSocket socketPorts = new ServerSocket(9000);
			
			//Player One initialization
			Socket p1Socket = socketPorts.accept();
			ArrayList<Pokemon> partyOne = generatePokemon();
			
			//Player Two initialization
			Socket p2Socket = socketPorts.accept();
			ArrayList<Pokemon> partyTwo = generatePokemon();

			//Arguments: Action#, Player#, PokemonList, CurrPokemon, Opponent Pokemon Image, Opponent Pokemon Name
			// Opponent Pokemon Curr HP, Opponent Pokemon Max HP, Opponent Pokemon still alive, Message, Damage Taken
			String imageOne = "./images/frontSprites/" + partyTwo.get(0).getName() + ".gif";
			ServerToClient p1Start = new ServerToClient(1, 1, partyOne, 1, imageOne, partyTwo.get(0).getName(),
					partyTwo.get(0).getCurrentHP(), partyTwo.get(0).getMaxHP(), 6, "", 0);
			
			String imageTwo = "./images/frontSprites/" + partyOne.get(0).getName() + ".gif";
			ServerToClient p2Start = new ServerToClient(1, 2, partyTwo, 1, imageTwo, partyOne.get(0).getName(),
					partyOne.get(0).getCurrentHP(), partyOne.get(0).getMaxHP(), 6, "", 0);
			
			ObjectOutputStream outToClientP1 = new ObjectOutputStream(p1Socket.getOutputStream());
			ObjectOutputStream outToClientP2 = new ObjectOutputStream(p2Socket.getOutputStream());		

			outToClientP1.writeObject(p1Start);
			outToClientP1.flush();
			
			outToClientP2.writeObject(p2Start);
			outToClientP2.flush();

			
			
			//Remove this while loop when you use the serverthreads
			//This loop is preventing the connections between server and clients from closing automatically
			while (true);
			
			
			//I recommend creating the inputstreams here also and then passing in
			//the streams instead of the sockets into server thread if you want
			//to leave the above code as is.
			
			/*
			ServerThread g1 = new ServerThread(p1Socket, p2Socket);
			ServerThread g2 = new ServerThread(p1Socket, p2Socket);
			
			g1.start();
			g2.start();
			*/
			
			
		} catch (Exception e) {
			System.out.println("Error in server");
			e.printStackTrace();
		}
		
		
	}
	
	class ServerThread extends Thread {
		private Socket p1Socket, p2Socket;
		
		ServerThread(Socket p1, Socket p2){
			p1Socket = p1;
			p2Socket = p2;
		}
		
		public void run(){
			while (true){}
		}
	}
	
	
	//Parsing functions
	private static void parseMoves(){
		try {
			Scanner moveReader = new Scanner(new File("./data/Moves.csv"));
			moveReader.useDelimiter("(,)|(\n)");
			while (moveReader.hasNext()){
				String moveName = moveReader.next();
				String moveType = moveReader.next();
				int movePower = Integer.parseInt(moveReader.next());
				int moveAccuracy = Integer.parseInt(moveReader.next());
				int isSpecial = Integer.parseInt(moveReader.next().trim());
				
				if (isSpecial == 0) //If the move is physical, special att = 0
					allMoves.put(moveName, new Move(moveName, moveType, movePower, 0, moveAccuracy));
				if (isSpecial == 1)
					allMoves.put(moveName, new Move(moveName, moveType, 0 , movePower, moveAccuracy));
				
			}
			moveReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error reading CSV File");
		}	
	}
	
	private static void parsePokemon(){
		try{
			Scanner pokemonReader = new Scanner(new File("./data/Pokemon.csv"));
			pokemonReader.useDelimiter("(,)|(\\n)");
			
			//Skipping first line
			for (int i=0; i<12; i++)
				pokemonReader.next();
			
			Integer ID = 1;
			while (pokemonReader.hasNext()){
				String pokemonName = pokemonReader.next();
				String pokemonType = pokemonReader.next();
				int HP = Integer.parseInt(pokemonReader.next());
				int attack = Integer.parseInt(pokemonReader.next());
				int defense = Integer.parseInt(pokemonReader.next());
				int spAttack = Integer.parseInt(pokemonReader.next());
				int spDefense = Integer.parseInt(pokemonReader.next());
				int speed = Integer.parseInt(pokemonReader.next());
				
				//Getting all four moves
				ArrayList<Move> currMoves = new ArrayList<Move>();
				for (int i=0; i<4; i++)
					currMoves.add(allMoves.get(pokemonReader.next().trim()));
				
				Pokemon tempPokemon = new Pokemon(pokemonName, pokemonType, HP, attack, defense, spAttack, spDefense, speed);
				tempPokemon.setMoves(currMoves);
				allPokemon.put(ID, tempPokemon);	
				
				ID++;
			}
			
			pokemonReader.close();
		} catch (Exception e){
			System.out.println("Error reading Pokemon File");
		}
	}
	
	
	//Generates six random Pokemon for a Player
	private static ArrayList<Pokemon> generatePokemon(){
		ArrayList<Pokemon> pokemonParty = new ArrayList<Pokemon>();
		
		while (pokemonParty.size() < 6){
			Pokemon insertPokemon = allPokemon.get(randInt(1, 150));
			if (pokemonParty.contains(insertPokemon))
				continue;
			
			pokemonParty.add(insertPokemon);			
		}
		
		return pokemonParty;
	}
	
	/**
	 * makes two players and populates them with six random pokemon
	 * by calling the method {@link generatePokemon()}
	 */
	private static void makePlayers(){
		playerOne = new Player(generatePokemon());
		playerTwo = new Player(generatePokemon());
	}
	
	/**
	 * Performs an attack on the other player. 
	 * @param playerOne true if player one is attacked by player two
	 * otherwise player two is attacked by player one
	 * @param move is the move determined by the CTS class sent to the server
	 * by the attacking player.
	 * @return true if the attack resulted in fainting the opponents
	 * pokemon false otherwise
	 * 
	 */
	private boolean attack(boolean playerOne, Move move){
		
		return false;
	}
	/**
	 * 
	 * checks to see if the player lost. Condition: all six of player's
	 * pokemon have fainted
	 * @param player true if playerOne is being checked false
	 * if player two is being checked
	 * @return true if the given player lost
	 * 
	 */
	private boolean playerLost(boolean player){
		if(player)
			return playerOne.getPlayablePokemon() > 0;
		else
			return  playerTwo.getPlayablePokemon() > 0;
	}
	
	/**
	 * calculates the amount of damage a certain move does to a player's pokemon
	 * @param player true if player one is attacked, otherwise playerTwo
	 * is being attacked
	 * @param move is the move determined by the CTS class sent to the server
	 * by the attacking player
	 * @return the amount of damage calculated
	 */
	private int calculateDamage(boolean player, Move move){
		return 0;
	}
	
	/**
	 * 
	 * @param typeOne
	 * @param typeTwo
	 * @return
	 */
	private double getTypeEffectiveness(int typeOne, int typeTwo){
		return 0.0;
	}
	
	//Extra functions for testing/randomizing
	private static void testParsers(){
		for (int i=1; i<allPokemon.size()+1; i++)
			allPokemon.get(i).printAllStats();;
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	
	
	public static void main (String [] args){
		//Parsing all Pokemon moves and Pokemon
		parseMoves();
		parsePokemon();
		
		//Start the server
		new Server();
		
		
	}
}
