/*
 * Team members: Brian Schwedock, Ryan Chen,
 * Allen Shi, Chris Holmes, Jonathan Luu, and Alejandro Lopez
 */

/**
 * ClientToServer is the class sent from the Client to the Server and
 * tells the server that an attack, switch, cancellation, or message
 * has been made
 */

package pokemon_simulator;

import java.io.Serializable;

public class ClientToServer implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	int action;
	String message;
	int moveChosen;
	int pokemonChosen;
	
	public ClientToServer (int action, String message,
			int moveChosen, int pokemonChosen) {
		this.action = action;
		this.message = message;
		this.moveChosen = moveChosen;
		this.pokemonChosen = pokemonChosen;
	}
}
