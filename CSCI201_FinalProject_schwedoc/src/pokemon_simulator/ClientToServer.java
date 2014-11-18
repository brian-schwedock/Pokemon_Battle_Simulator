package pokemon_simulator;

public class ClientToServer {
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
