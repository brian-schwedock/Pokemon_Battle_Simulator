package pokemon_simulator;

import java.util.ArrayList;
/**
 * 
 * @author askem
 * @author Bcssd1234
 *
 */

public class Player {
	// list of six pokemon in the player's party. will be determined by random
	// at the start of each battle
	private ArrayList<Pokemon> allPokemon;
	// an integer range [1,6] inclusive representing the current pokemon in play
	private int currentPokemon;
	// an integer [0,6] representing the amount of playale pokemon (not fainted)
	// in the player's party
	private int playablePokemon;
	
	/**
	 * 
	 * @param 			pokemon the ArrayList of six random pokemon given by the 
	 * 					server class. These six pokemon represent the player's "party."
	 * 					Pokemon can be referred to by their index in the ArrayList offset
	 * 					by one. For example, the first pokemon in the player's party 
	 * 					can be referred to by "1" instead of "0"
	 */
	public Player(ArrayList<Pokemon> pokemon){
		allPokemon = pokemon;
	}
	
	/**
	 * Changes the player's current pokemon in play. The current
	 * pokemonin play is the one that is in the battle. 
	 * @param 		pokemonIndex an integer [1,6] that represents one of
	 * 				the pokemon in teh player's party.
	 */
	public void setCurrentPokemon(int pokemonIndex){
		currentPokemon = pokemonIndex;
	}
	
	/**
	 * returns the amount of playable pokemon left in the player's party
	 * @return			the amount of playablePokemon in the party
	 */
	public int getPlayablePokemon(){
		return playablePokemon;
	}
	

}
