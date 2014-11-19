package pokemon_simulator;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerListener extends Thread {
	ObjectInputStream ois;
	GameApplication ga;
	
	public ServerListener (ObjectInputStream ois, GameApplication ga) {
		this.ois = ois;
		this.ga = ga;
	}
	
	public void run () {
		ServerToClient stc = null;
		try {
			stc = (ServerToClient) ois.readObject ();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (stc.action == 1){
			ga.addMessage(stc.message);
		}
		else {
			//ga.setAllPokemon ();
			//ga.setCurrentPokemon ();
			//ga.setOpposingPokemonImage ();
			//ga.setOpposingPokemonCurrentHP ();
			//ga.setOpposingPokemonMaxHP ();
			//ga.setOpposingPokemonAlive ();
			
			//Add a message indicating damage percentage lost
			//and whether a Pokemon has fainted
			
			//And some additional info based on value of action
		}
	}
}
