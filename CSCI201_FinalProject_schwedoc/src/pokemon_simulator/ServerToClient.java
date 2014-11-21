package pokemon_simulator;

import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;

public class ServerToClient implements Serializable {

	private static final long serialVersionUID = 1L;
	
	int action;
	int playerNumber;
	ArrayList<Pokemon> allPokemon;
	int pokemonInPlay;
	Image opposingPokemonImage;
	int opposingCurrentHP;
	int opposingMaxHP;
	int opposingPokemonAlive;
	String message;
	int damageTaken;
	
	public ServerToClient (int action, int playerNumber,
			ArrayList<Pokemon> allPokemon, int pokemonInPlay,
			Image opposingPokemonImage, int opposingCurrentHP,
			int opposingMaxHP, int opposingPokemonAlive, String message,
			int damageTaken) {
		this.action = action;
		this.playerNumber = playerNumber;
		this.allPokemon = allPokemon;
		this.pokemonInPlay = pokemonInPlay;
		this.opposingPokemonImage = opposingPokemonImage;
		this.opposingCurrentHP = opposingCurrentHP;
		this.opposingMaxHP = opposingMaxHP;
		this.opposingPokemonAlive = opposingPokemonAlive;
		this.message = message;
		this.damageTaken = damageTaken;
	}
}
