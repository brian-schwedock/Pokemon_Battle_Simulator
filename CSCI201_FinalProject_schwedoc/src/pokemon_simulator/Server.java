package pokemon_simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server {
	static Map<String, Move> allMoves = new HashMap<String,Move>();
	static Map<Integer, Pokemon> allPokemon = new HashMap<Integer, Pokemon>();
	
	
	//Main constructor - Sets up the server socket and waits for two players to join
	Server(){
		try {
			ServerSocket socketPorts = new ServerSocket(9000);
			
			//Wait for player one to join
			Socket p1Socket = socketPorts.accept();
			
		} catch (Exception e) {}
		
		
	}
	
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
		} catch (Exception e){
			System.out.println("Error reading Pokemon File");
		}
	}
	
	private static void testParsers(){
		for (int i=1; i<allPokemon.size()+1; i++)
			allPokemon.get(i).printAllStats();;
	}
	
	public static void main (String [] args){
		//Parsing all Pokemon moves and Pokemon
		parseMoves();
		parsePokemon();
		testParsers();
		
		//Start the server
		//new Server();
		
		
	}
}
