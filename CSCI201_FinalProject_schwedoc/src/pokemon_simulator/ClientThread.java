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

		while(true){
			
		try {
			stc = (ServerToClient) ois.readObject ();
			
			//System.out.println("read in client to server class");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String opposingPlayerName = "Player 1";
		if (stc.playerNumber == 1)
			opposingPlayerName = "Player 1";
		else if(stc.playerNumber == 2)
			opposingPlayerName = "Player 2";
		
		if (stc.action == 1){
			//System.out.println("Client Class adding a message");
			
			ga.addMessage(stc.message, opposingPlayerName);
		}
		else {
 			for (Pokemon k: stc.allPokemon)
 				k.setImages();
 			
			ga.setAllPokemon(stc.allPokemon);
			ga.setCurrentPokemon(stc.pokemonInPlay);
			System.out.println(ga.getPokemonName());
			ga.setOpposingPokemonImage (stc.opposingPokemonImage);
			ga.setOpposingPokemonCurrentHP (stc.opposingCurrentHP);
			ga.setOpposingPokemonMaxHP (stc.opposingMaxHP);
			ga.setOpposingPokemonAlive (stc.opposingPokemonAlive);
			ga.setOpposingPokemonName(stc.opposingPokemonName);
			ga.addMessage(""+ stc.damageTaken + " damage to player " + opposingPlayerName, "");
			ga.repaint();
			//Add a message indicating damage percentage lost
			//and whether a Pokemon has fainted
			
			//And some additional info based on value of action
		}
		}
	}
}
