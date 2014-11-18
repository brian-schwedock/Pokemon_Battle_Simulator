package pokemon_simulator;

import java.util.ArrayList;

import javax.swing.ImageIcon;

public class ServerToClient {
	int action;
	int playerNumber;
	ArrayList<Pokemon> allPokemon;
	int pokemonInPlay;
	ImageIcon opposingPokemonImage;
	int opposingCurrentHP;
	int opposingMaxHP;
	int opposingPokemonAlive;
	String message;
	int damageTaken;
	
	public ServerToClient (int action, int playerNumber,
			ArrayList<Pokemon> allPokemon, int pokemonInPlay,
			ImageIcon opposingPokemonImage, int opposingCurrentHP,
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
