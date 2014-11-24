package pokemon_simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	static Map<String, Integer> movePairs = new HashMap<String, Integer>();
	
	private static Player playerOne;
	private static Player playerTwo;
	
	private ObjectOutputStream outToClientP1;
	private ObjectOutputStream outToClientP2;
	
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
	
	private double typeChart[][] = {{1,1,1,1,1,1,1,1,1,1,1,1,0.5,0,1},
										{1,0.5,0.5,2,1,2,1,1,1,1,1,2,0.5,1,0.5},
										{1,2,0.5,0.5,1,1,1,1,2,1,1,1,2,1,0.5},
										{1,0.5,2,0.5,1,1,1,0.5,2,0.5,1,0.5,2,1,0.5},
										{1,1,2,0.5,0.5,1,1,1,0,2,1,1,1,1,0.5},
										{1,0.5,0.5,2,1,0.5,1,1,2,2,1,1,1,1,2},
										{2,1,1,1,1,2,1,0.5,1,0.5,0.5,0.5,2,0,1},
										{1,1,1,2,1,1,1,0.5,0.5,1,1,1,0.5,0.5,1},
										{1,2,1,0.5,2,1,1,2,1,0,1,0.5,2,1,1,1},
										{1,1,1,2,0.5,1,2,1,1,1,1,2,0.5,1,1},
										{1,1,1,1,1,1,2,2,1,1,0.5,1,1,1,1},
										{1,0.5,1,2,1,1,0.5,0.5,1,0.5,2,1,1,0.5,1},
										{1,2,1,1,1,2,0.5,1,0.5,2,1,2,1,1,1},
										{0,1,1,1,1,1,1,1,1,1,1,2,1,1,2},
										{1,1,1,1,1,1,1,1,1,1,1,1,1,1,2}};
	
	
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
			
			outToClientP1 = new ObjectOutputStream(p1Socket.getOutputStream());
			outToClientP2 = new ObjectOutputStream(p2Socket.getOutputStream());		

			outToClientP1.writeObject(p1Start);
			outToClientP1.flush();
			
			outToClientP2.writeObject(p2Start);
			outToClientP2.flush();
			
			// initialize the move pairings with integers
			movePairs.put("Normal", 0);
			movePairs.put("Fire", 1);
			movePairs.put("Water",2);
			movePairs.put("Grass", 3);
			movePairs.put("Electric", 4);
			movePairs.put("Ice", 5);
			movePairs.put("Fighting", 6);
			movePairs.put("Poison", 7);
			movePairs.put("Ground", 8);
			movePairs.put("Flying", 9);
			movePairs.put("Psychic", 10);
			movePairs.put("Bug", 11);
			movePairs.put("Rock", 12);
			movePairs.put("Ghost", 13);
			movePairs.put("Dragon", 14);

			
			
			//Remove this while loop when you use the serverthreads
			//This loop is preventing the connections between server and clients from closing automatically
			while (true);
			
			
			//I recommend creating the inputstreams here also and then passing in
			//the streams instead of the sockets into server thread if you want
			//to leave the above code as is.
			
			/*
			ServerThread st1 = new ServerThread(this, new ObjectInputStream(p1Socket.getInputStream()));
			ServerThread st2 = new ServerThread(this, new ObjectInputStream(p2Socket.getInputStream()));
			
			st1.start();
			st2.start();
			*/
			
			
		} catch (Exception e) {
			System.out.println("Error in server");
			e.printStackTrace();
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
				System.out.println(moveAccuracy);
				int isSpecial = Integer.parseInt(moveReader.next());
				
				allMoves.put(moveName, new Move(moveName, moveType, movePower, isSpecial, moveAccuracy));
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
	 * <p>
	 * <li>Dmg = ( [0.84] * [{Attack(attacking) * AttackStat(attacking)} / Defense or SpDefense(defending)] /50 + 2) * Modifier
	 * <li>attack: the attack power of the move used
	 * <li>attackStat: the attack stat of the attacking pokemon. Will be SpAttack or just Attack based on  the type of the attacking move used. 
	 * <li>Defense or SpDefense: defense stat of the defending pokemon. Will be based on the type of the
	 *  type of the attacking move used. Ex: if opponent uses SpAttack then defense stat will be 
	 *  SpDefense and vice versa with just Attack. 
	 * <li>modifer is based on the type effectiveness and STAB quality of the move
	 * <li>resulting damage is rounded down and truncated (no remainders or decimals)
	 * @param player true if player one is attacked, otherwise playerTwo
	 * is being attacked
	 * @param move is the move determined by the CTS class sent to the server
	 * by the attacking player
	 * @return the amount of damage calculated
	 */
	private int calculateDamage(boolean player, Move move){
		Pokemon defendingPokemon;
		Pokemon attackingPokemon;
		
		Map<String,Integer> defendingStats;
		Map<String, Integer> attackingStats;
		if(player){
			defendingPokemon = playerOne.getPokemon(1);
			attackingPokemon = playerTwo.getPokemon(1);
		}else{
			defendingPokemon = playerTwo.getPokemon(1);
			attackingPokemon = playerOne.getPokemon(1);
		}
		defendingStats = defendingPokemon.getAllStats();
		attackingStats = attackingPokemon.getAllStats();
		int moveIntType = movePairs.get(move.getType());
		int defendingType = movePairs.get(defendingPokemon.getType());
		int attackingType = movePairs.get(attackingPokemon.getType());
		
		double typeEffectiveness = getTypeEffectiveness(moveIntType, defendingType);
		double stabMultiplier = getStab(moveIntType, attackingType);
		double modifier = typeEffectiveness * stabMultiplier * (Math.random() + 0.86);
		
		double damage =  (0.84 *((move.getAttackPower() * attackingStats.get("Attack") ) / defendingStats.get("Defense")) / 50 + 2) * modifier;
		
		return (int)damage;
	}
	
	/**
	 * returns the type effectiveness of typeOne over typeTwo
	 * <p>
	 * Ex: if type one is FIRE and type two is GRASS then there is 2X type 
	 * effectiveness. Look at the chart given above to see a full list of type 
	 * effectiveness rankings.
	 * Type effectiveness should be stored in a 2D array that represents the 
	 * chart given above
	 * @param typeOne the type of the attacking pokemon
	 * @param typeTwo the type of the defending pokemon
	 * @return the type effectiveness caused by the move on the 
	 * player's pokemon. values are in the range {0.0,0.5,1.0,2.0}
	 */
	private double getTypeEffectiveness(int typeOne, int typeTwo){
		return typeChart[typeOne][typeTwo];
	}
	
	/**
	 * gets the damage modifer for the given type
	 * @param typeOne
	 * @param typeTwo
	 * @return the damage modifier 
	 */
	private double getModifier(int typeOne, int typeTwo){
		
		return 0.0;
		
	}
	
	/**
	 * a move has STAB if the moveType and the pokemonType are the same 
	 * @param moveType is the type of the attacking move
	 * @param pokemonType is the type of the attacking pokemon
	 * @return return stab multipliers
	 */
	private double getStab(int moveType, int pokemonType){
		if( moveType == pokemonType)
			return 1.5;
		else
			return 1.0;
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
