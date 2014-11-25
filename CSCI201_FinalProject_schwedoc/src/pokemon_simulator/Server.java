package pokemon_simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	
	private ArrayList<Pokemon> partyOne;
	private ArrayList<Pokemon> partyTwo;
	// true if playerOne wins the game false otherwise
	private boolean playerOneVictory;
	// true if playerTwo wins the game false otherwise
	private boolean playerTwoVictory;
	// true if the game is still going on meaning neither player has won
	private boolean gameOn;
	
	String imageOne;
	String imageTwo;
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
	
	/**
	 * 2D Array in which each index represents the type effectiveness of the column type 
	 * over the row type. For example, index [1][1] represents the type effectivenss of 
	 * Normal vs. Normal where type Normal is represented as 0. 
	 * The integer representations of types are as follows
	 * <p>
	 * <li> Fire = 1
	 * <li> Water = 2
	 * <li> Grass = 3
	 * <li> Electric = 4
	 * <li> Ice = 5
	 * <li> Fighting = 6
	 * <li> Poison = 7
	 * <li> Ground = 8
	 * <li> Flying = 9
	 * <li> Psychic = 10
	 * <li> Bug = 11
	 * <li> Rock = 12
	 * <li> Ghost = 13
	 * <li> Dragon = 14
	 */
	
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
	
	
	private ClientToServer ctsOne;
	private ClientToServer ctsTwo;
	
	public static boolean playerOneMadeMove = false;
	public static boolean playerTwoMadeMove = false;
	
	Server(){
		try {
			ServerSocket socketPorts = new ServerSocket(9000);
			
			//Player One initialization
			Socket p1Socket = socketPorts.accept();
			partyOne = generatePokemon();
			
			//Player Two initialization
			Socket p2Socket = socketPorts.accept();
			partyTwo = generatePokemon();

			//Arguments: Action#, Player#, PokemonList, CurrPokemon, Opponent Pokemon Image, Opponent Pokemon Name
			// Opponent Pokemon Curr HP, Opponent Pokemon Max HP, Opponent Pokemon still alive, Message, Damage Taken
			imageOne = "./images/frontSprites/" + partyTwo.get(0).getName() + ".gif";
			ServerToClient p1Start = new ServerToClient(1, 1, partyOne, 1, imageOne, partyTwo.get(0).getName(),
					partyTwo.get(0).getCurrentHP(), partyTwo.get(0).getMaxHP(), 6, "", 0, "");
			
			imageTwo = "./images/frontSprites/" + partyOne.get(0).getName() + ".gif";
			ServerToClient p2Start = new ServerToClient(1, 2, partyTwo, 1, imageTwo, partyOne.get(0).getName(),
					partyOne.get(0).getCurrentHP(), partyOne.get(0).getMaxHP(), 6, "", 0, "");
			
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
			//while (true);
			
			
			//I recommend creating the inputstreams here also and then passing in
			//the streams instead of the sockets into server thread if you want
			//to leave the above code as is.
			
			
			ServerThread st1 = new ServerThread(this, new ObjectInputStream(p1Socket.getInputStream()),1);
			ServerThread st2 = new ServerThread(this, new ObjectInputStream(p2Socket.getInputStream()),2);
			
			st1.start();
			st2.start();
			
			
			
		} catch (Exception e) {
			System.out.println("Error in server");
			e.printStackTrace();
		}
		
		
	}
	private int returnAlive(int playerNumber)
	{
		int i = 0;
		if(playerNumber == 1)
		{
			for(Pokemon x: partyOne)
				if(!x.isFainted())
					i++;
		}
		else
		{
			for(Pokemon x: partyTwo)
				if(!x.isFainted())
					i++;
		}
		return i;
	}
	
	/**
	 * makes the appropriate moves based on the CTS(client to server) class given. 
	 * This only occurs once both players have chosen an action 
	 * (move, or change pokemon) specified with integers 2 and 3 
	 * respectively in the CTS class. This method will call all the other methods
	 * needed to make player moves such as calculate damage, switchPokemon. 
	 */
	public void makePlayerMoves(){
		System.out.println("Both players have made an action, now making player moves");
		ServerToClient stcSwitching;
		ServerToClient stcNotSwitching;
		if(ctsOne.action == 4){
			stcNotSwitching = new ServerToClient(2, 2, partyTwo, 1, imageOne, partyOne.get(0).getName(), 
					partyOne.get(0).getCurrentHP(), partyOne.get(0).getMaxHP(), 6, "", 0, "");
			
			sendSTC(stcNotSwitching, false);
		}
		
		if(ctsTwo.action == 4){
			// send stcTwo to player who did not switch pokemon in this case Player ONE
			stcNotSwitching = new ServerToClient(2 , 1, partyOne, 1, imageTwo,partyTwo.get(0).getName(), 
					partyTwo.get(0).getCurrentHP(), partyTwo.get(0).getMaxHP(), 6, "", 0, "");
			sendSTC(stcNotSwitching, true);
		}
		if(ctsOne.action == 3){
			switchPokemon(ctsOne.pokemonChosen - 1, 1);
			int playerOneDamageTaken = 0;
			int playerTwoDamageTaken = 0;
			imageOne = "./images/frontSprites/" + partyOne.get(0).getName() + ".gif";
			imageTwo = "./images/frontSprites/" + partyTwo.get(0).getName() + ".gif";
			
			// send stcOne to player who switched pokemon. In this case Player ONE
			stcSwitching = new ServerToClient(3, 1, partyOne, 1, imageTwo,partyTwo.get(0).getName(), 
					partyTwo.get(0).getCurrentHP(), partyTwo.get(0).getMaxHP(), 6, "", playerOneDamageTaken, "");
			// send stcTwo to player who did not switch pokemon in this case Player TWO
			stcNotSwitching = new ServerToClient(2, 2, partyTwo, 1, imageOne, partyOne.get(0).getName(), 
					partyOne.get(0).getCurrentHP(), partyOne.get(0).getMaxHP(), 6, "", playerTwoDamageTaken, "");
			sendSTC(stcSwitching, true);
			sendSTC(stcNotSwitching, false);
			System.out.println("Switching playerOne pokemon");
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		if(ctsTwo.action == 3){
			switchPokemon(ctsTwo.pokemonChosen - 1, 2);
			int playerOneDamageTaken = 0;
			int playerTwoDamageTaken = 0;
			imageOne = "./images/frontSprites/" + partyOne.get(0).getName() + ".gif";
			imageTwo = "./images/frontSprites/" + partyTwo.get(0).getName() + ".gif";
			
			// send stcOne to player who switched pokemon. In this case Player TWO
			stcSwitching = new ServerToClient(3, 2, partyTwo, 1, imageOne,partyOne.get(0).getName(), 
					partyOne.get(0).getCurrentHP(), partyOne.get(0).getMaxHP(), 6, "", playerOneDamageTaken, "");
			// send stcTwo to player who did not switch pokemon in this case Player ONE
			stcNotSwitching = new ServerToClient(2 , 1, partyOne, 1, imageTwo,partyTwo.get(0).getName(), 
					partyTwo.get(0).getCurrentHP(), partyTwo.get(0).getMaxHP(), 6, "", playerTwoDamageTaken, "");
			sendSTC(stcNotSwitching, true);
			sendSTC(stcSwitching, false);
			System.out.println("Switching playerTwo pokemon");
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		resetActionCount();
		playerOneMadeMove = false;
		playerTwoMadeMove = false;
		/*if(ctsOne.action == 3 && ctsTwo.action == 3){
			
			int playerOneDamageTaken = 0;
			int playerTwoDamageTaken = 0;
			
			imageOne = "./images/frontSprites/" + partyOne.get(0).getName() + ".gif";
			imageTwo = "./images/frontSprites/" + partyTwo.get(0).getName() + ".gif";
			stcOne = new ServerToClient(ctsOne.action, 1, partyOne, 1, imageTwo,partyTwo.get(0).getName(), 
					partyTwo.get(0).getCurrentHP(), partyTwo.get(0).getMaxHP(), 6, "", playerOneDamageTaken);
			stcTwo = new ServerToClient(ctsTwo.action, 2, partyTwo, 1, imageOne,partyOne.get(0).getName(), 
					partyOne.get(0).getCurrentHP(), partyOne.get(0).getMaxHP(), 6, "", playerTwoDamageTaken);
			sendSTC(stcOne, true);
			sendSTC(stcTwo, false);
			
			// turn is now over reset control values
			resetActionCount();
			playerOneMadeMove = false;
			playerTwoMadeMove = false;
		}	
		*/
		/*else {
			int playerOneSpeed = partyOne.get(0).getAllStats().get("Speed");
			int playerTwoSpeed = partyTwo.get(0).getAllStats().get("Speed");
			
			int playerOneDamageTaken = 0;
			int playerTwoDamageTaken = 0;
			boolean missed;
			boolean lose;
			if(playerOneSpeed>playerTwoSpeed ){
				if( ctsOne.action == 2){
					int player2currentHp=partyTwo.get(0).getCurrentHP();
					missed=attack(false , partyOne.get(0).getMoves().get(ctsOne.moveChosen-1));
					playerTwoDamageTaken=player2currentHp-partyTwo.get(0).getCurrentHP();
					lose=playerLost(false);//check loss
					if(lose){
						//game ends
					}
					if(partyTwo.get(0).getCurrentHP()==0){//checks faint
						
					}
				}
				if( ctsTwo.action == 2){
					int player1currentHp=partyOne.get(0).getCurrentHP();
					missed=attack(true , partyTwo.get(0).getMoves().get(ctsTwo.moveChosen-1));
					playerOneDamageTaken=player1currentHp-partyOne.get(0).getCurrentHP();
					lose=playerLost(true);//check loss
					if(lose){
						
					}
					if(partyOne.get(0).getCurrentHP()==0){
						
					}
				}
				
			}
			else if (playerTwoSpeed>playerOneSpeed){
				if( ctsTwo.action == 2){
					int player1currentHp=partyOne.get(0).getCurrentHP();
					missed=attack(true , partyTwo.get(0).getMoves().get(ctsTwo.moveChosen-1));
					playerOneDamageTaken=player1currentHp-partyOne.get(0).getCurrentHP();
					lose=playerLost(true);//check loss
					if(lose){
						
					}
					if(partyOne.get(0).getCurrentHP()==0){
						
					}
				}
				if( ctsOne.action == 2){
					int player2currentHp=partyTwo.get(0).getCurrentHP();
					missed=attack(false , partyOne.get(0).getMoves().get(ctsOne.moveChosen-1));
					playerTwoDamageTaken=player2currentHp-partyTwo.get(0).getCurrentHP();
					lose=playerLost(false);//check loss
					if(lose){
						//game ends
					}
					if(partyTwo.get(0).getCurrentHP()==0){//checks faint
						
					}
				}
			}
		}*/
		
	}
	void switchPokemon(int number,int playerNumber)
	{	
		
		
		if(playerNumber == 1)
		{
			Pokemon temp = partyOne.get(number);
			partyOne.set(number,partyOne.get(0));
			partyOne.set(0,temp);
		/*	ServerToClient stc = new ServerToClient(1, 1, partyOne, 1, imageOne, partyTwo.get(0).getName(),
					partyTwo.get(0).getCurrentHP(), partyTwo.get(0).getMaxHP(), returnAlive(2), "", 0);
			try {
				outToClientP1.writeObject(stc);
				outToClientP1.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		else if(playerNumber == 2)
		{
			Pokemon temp = partyTwo.get(number);
			partyTwo.set(number,partyTwo.get(0));
			partyTwo.set(0,temp);
			
			/*ServerToClient stc = new ServerToClient(1, 1, partyOne, 1, imageOne, partyTwo.get(0).getName(),
					partyTwo.get(0).getCurrentHP(), partyTwo.get(0).getMaxHP(), returnAlive(1), "", 0);
			try {
				outToClientP2.writeObject(stc);
				outToClientP2.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
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
				String tempIsSpecial;
				tempIsSpecial=moveReader.nextLine();
				StringBuilder sb = new StringBuilder(tempIsSpecial);
				sb.deleteCharAt(0);
				int isSpecial = Integer.parseInt(sb.toString());
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
	 * @param player true if player one is attacked by player two
	 * otherwise player two is attacked by player one
	 * @param move is the move determined by the CTS class sent to the server
	 * by the attacking player.
	 * @return true if the attack missed
	 * pokemon false otherwise
	 * 
	 */
	private boolean attack(boolean player, Move move){
		
		int dmg = calculateDamage(player, move);
		
		//player1 attacked by player 2
		if(player)
		{
			if(dmg == -1)
			{
				dmg=0;
				partyOne.get(0).setCurrentHP(partyOne.get(0).getCurrentHP()-dmg);
				return true;
			}
			
			else
			{
				if(dmg >= partyOne.get(0).getCurrentHP()){
					partyOne.get(0).setCurrentHP(dmg);
					return false;
				}
				else{
					partyOne.get(0).setCurrentHP(partyOne.get(0).getCurrentHP()-dmg);
					return false;
				}
			}
		}
		
		//p2 attacked by p1
		else
		{
			if(dmg == -1)
			{
				dmg=0;
				partyTwo.get(0).setCurrentHP(partyTwo.get(0).getCurrentHP()-dmg);
				return true;
			}
			
			else
			{
				if(dmg >= partyTwo.get(0).getCurrentHP()){
					partyTwo.get(0).setCurrentHP(dmg);
					return false;
				}
				else{
					partyTwo.get(0).setCurrentHP(partyTwo.get(0).getCurrentHP()-dmg);
					return false;
				}
			}
		}
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
			return (playerOne.getPlayablePokemon() > 0);
		else
			return  (playerTwo.getPlayablePokemon() > 0);
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
			defendingPokemon = partyOne.get(0);
			attackingPokemon = partyTwo.get(0);
		}else{
			defendingPokemon = partyTwo.get(0);
			attackingPokemon = partyOne.get(0);
		}
		defendingStats = defendingPokemon.getAllStats();
		attackingStats = attackingPokemon.getAllStats();
		int moveIntType = movePairs.get(move.getType());
		int defendingType = movePairs.get(defendingPokemon.getType());
		int attackingType = movePairs.get(attackingPokemon.getType());
		double damage;
		if(randInt(0,100)>=move.getAccuracy()){

			double typeEffectiveness = getTypeEffectiveness(moveIntType, defendingType);
			double stabMultiplier = getStab(moveIntType, attackingType);
			double modifier = typeEffectiveness * stabMultiplier * (Math.random() + 0.86);
			
			damage =  (0.84 *((move.getAttackPower() * attackingStats.get("Attack") ) / defendingStats.get("Defense")) / 50 + 2) * modifier;
		}
		else {
			//missed
			damage=-1;
		}
		
		
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
	/**
	 * sets the client to server class to the appropriate player
	 * @param player true if cts belongs to playerOne, in which case 
	 * set ctsOne to cts. otherwise cts belongs to playerTwo, in which case set
	 * ctsTwo to cts;
	 * @param cts the client to server class object from server thread
	 */
	public void setCTS(boolean player, ClientToServer cts){
		if(player){
			ctsOne = cts;
		}else{
			ctsTwo = cts;
		}
	}
	
	/**
	 * increments playerAction. Note this value should not go above 2
	 * For explanation of what justifies an action see {@link resetActionCount()}
	 */
	public void incrementActionCount(){
		actionCount++;
	}
	
	public int getActionCount(){
		return actionCount;
	}
	
	/**
	 * At the end of every turn action count should be reset to 0.
	 * A turn is when both players complete an action. An action
	 * is either an attack, or a pokemon switch. A chat message is not
	 * a player action
	 */
	public void resetActionCount(){
		actionCount = 0;
	}
	
	/**
	 * Sends chat message to player two
	 * @param cts is the given chat message to player one
	 */
	public void sendMessageToPlayerTwo(ClientToServer cts){
		try {
			System.out.println("sendMessage to player two");
			outToClientP2.writeObject(new ServerToClient(1, 2, null, 0, null,null,0, 0, 0, cts.message, 0, ""));
			outToClientP2.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends chat message to player one 
	 * @param cts is the given cts class from player two 
	 */
	public void sendMessageToPlayerOne(ClientToServer cts){
		try {
			System.out.println("sendMessage to player one");
			outToClientP1.writeObject(new ServerToClient(1, 1, null, 0, null,null,0, 0, 0, cts.message, 0, ""));
			outToClientP1.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// TODO send a STC class once the action count has reached 2
	public void sendSTC(ServerToClient stc, boolean player){
		try{
			if(player){
				outToClientP1.reset();
				outToClientP2.reset();
				System.out.println("Sending stc to client one");
				outToClientP1.writeObject(stc);
				outToClientP1.flush();
			}else{
				outToClientP2.reset();
				outToClientP1.reset();
				outToClientP2.writeObject(stc);
				outToClientP2.flush();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void main (String [] args){
		//Parsing all Pokemon moves and Pokemon
		parseMoves();
		parsePokemon();
		
		//Start the server
		new Server();

	}
}
