package pokemon_simulator;

import java.util.ArrayList;

public class Player {
	// list of six pokemon in the player's party. will be determined by random
	// at the start of each battle
	private ArrayList<Pokemon> allPokemon;
	// an integer range [1,6] inclusive representing the current pokemon in play
	private int currentPokemon;
	// an integer [0,6] representing the amount of playale pokemon (not fainted)
	// in the player's party
	private int playablePokemon;
	public Player(){
		
	}

}
