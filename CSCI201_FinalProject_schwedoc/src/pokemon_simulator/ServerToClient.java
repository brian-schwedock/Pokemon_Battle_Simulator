package pokemon_simulator;

import java.io.Serializable;
import java.util.ArrayList;

public class ServerToClient implements Serializable {

	//private static final long serialVersionUID = 1L;
	
	int action;
	int playerNumber;
	ArrayList<Pokemon> allPokemon;
	int pokemonInPlay;
	String opposingPokemonName;
	String opposingPokemonImage;
	int opposingCurrentHP;
	int opposingMaxHP;
	int opposingPokemonAlive;
	String message;
	int damageTaken;
	String attackName;
	
	public ServerToClient (int action, int playerNumber, ArrayList<Pokemon> allPokemon, 
			int pokemonInPlay, String opposingPokemonImage, String opposingPokemonName, 
			int opposingCurrentHP,int opposingMaxHP, int opposingPokemonAlive, String message,
			int damageTaken, String attackName) {
		this.action = action;
		this.playerNumber = playerNumber;
		this.allPokemon = allPokemon;
		this.pokemonInPlay = pokemonInPlay;
		this.opposingPokemonImage = opposingPokemonImage;
		this.opposingPokemonName = opposingPokemonName;
		this.opposingCurrentHP = opposingCurrentHP;
		this.opposingMaxHP = opposingMaxHP;
		this.opposingPokemonAlive = opposingPokemonAlive;
		this.message = message;
		this.damageTaken = damageTaken;
		this.attackName = attackName;
	}
}
