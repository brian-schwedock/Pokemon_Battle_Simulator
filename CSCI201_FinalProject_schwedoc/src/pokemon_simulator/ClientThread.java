//Changed name from ServerListener to ClientThread

//The job of this class is to listen to messages (ServerToClient)
//and call the corresponding functions in GameApplication

//This class will never send information to the Server
//GameApplication will be the only class to send information to the Server

package pokemon_simulator;

import java.awt.CardLayout;
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
		
		//This variable is to control when to change the bottom
		//panel back to action panel.  Increment when action is
		//2,3,4,5.  If 6 or 7, set right to 2.  When it is 2,
		//change the bottom panel;
		int changeBottomPanel = 0;

		while(true){
			
			try {
				stc = (ServerToClient) ois.readObject ();
				
				//System.out.println("read in client to server class");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String opposingPlayerName = null;
			if (stc.playerNumber == 1)
				opposingPlayerName = "Player 2";
			else
				opposingPlayerName = "Player 1";
			
			
			//System.out.println ("action:" + stc.action);
			
			if (stc.action == 1){
				//System.out.println("Client Class adding a message");
				
				ga.addChatMessage(stc.message, opposingPlayerName);
			}
			else if (stc.action == 2){
				ga.setOpposingPokemonImage(stc.opposingPokemonImage);
				ga.setOpposingPokemonCurrentHP (stc.opposingCurrentHP);
				ga.setOpposingPokemonMaxHP (stc.opposingMaxHP);
				ga.setOpposingPokemonName(stc.opposingPokemonName);
				//ga.repaint();
				
				ga.addMessage(opposingPlayerName + " switched Pokemon");
				
				changeBottomPanel++;
			}
			else if (stc.action == 3){
				ga.setAllPokemon(stc.allPokemon);
				ga.resetBottomPanel();
				//ga.repaint();
				
				ga.addMessage("You switched Pokemon");
				
				changeBottomPanel++;
			}
			else if (stc.action == 4){
				
			}
			else if (stc.action == 5){
				
			}
			else if (stc.action == 6){
				
			}
			else if (stc.action == 7){
				
			}
			else if (stc.action == 8){
				
			}
			else {  //stc.action == 9
				
			}
			
			/*
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
			
			if(stc.damageTaken > 0)
			{ga.addMessage(""+ stc.damageTaken + " damage to player " + opposingPlayerName, "");}
			
			ga.updateSwitchButtons();
			ga.updateAttackButtons();
			ga.resetBottomPanel();
			//ga.repaint();
			//Add a message indicating damage percentage lost
			//and whether a Pokemon has fainted
			
			//And some additional info based on value of action
			 */
			
			if (changeBottomPanel >= 2){
				ga.changeBottomPanel (true);
				changeBottomPanel = 0;
			}
		}
	}
}