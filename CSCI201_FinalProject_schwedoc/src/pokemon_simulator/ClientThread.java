//Changed name from ServerListener to ClientThread

//The job of this class is to listen to messages (ServerToClient)
//and call the corresponding functions in GameApplication

//This class will never send information to the Server
//GameApplication will be the only class to send information to the Server

package pokemon_simulator;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ClientThread extends Thread {
	ObjectInputStream ois;
	GameApplication ga;
	
	public ClientThread (ObjectInputStream ois, GameApplication ga) {
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
