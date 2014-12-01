/*
 * Team members: Brian Schwedock, Ryan Chen,
 * Allen Shi, Chris Holmes, Jonathan Luu, and Alejandro Lopez
 */

/**
 *The job of this class is to listen to messages (ServerToClient)
 *and call the corresponding functions in GameApplication
 *
 *This class will never send information to the Server
 *GameApplication will be the only class to send information to the Server
*/

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
		
		//This variable is to control when to change the bottom
		//panel back to action panel.  Increment when action is
		//2,3,4,5.  If 6 or 7, set right to 2.  When it is 2,
		//change the bottom panel;
		int changeBottomPanel = 0;

		while(true){
			
			try {
				stc = (ServerToClient) ois.readObject ();
			} catch (ClassNotFoundException e) {
				//e.printStackTrace();
			} catch (IOException e) {
				//e.printStackTrace();
			}
			
			String opposingPlayerName = null;
			if (stc.playerNumber == 1)
				opposingPlayerName = "Player 2";
			else
				opposingPlayerName = "Player 1";
		
			
			if (stc.action == 1) {
				//Opposing player sent a chat message
				
				ga.addChatMessage(stc.message, opposingPlayerName);
			}
			else if (stc.action == 2) {
				//Opposing player switched Pokemon
				
				ga.setOpposingPokemonImage(stc.opposingPokemonImage);
				ga.setOpposingPokemonCurrentHP (stc.opposingCurrentHP);
				ga.setOpposingPokemonMaxHP (stc.opposingMaxHP);
				ga.setOpposingPokemonName(stc.opposingPokemonName);
				
				ga.addMessage(opposingPlayerName + " sent out " + stc.opposingPokemonName + "!");
				
				changeBottomPanel++;
				ga.enableCancel(false);
			}
			else if (stc.action == 3) {
				//You switched Pokemon
				
				ga.setAllPokemon(stc.allPokemon);
				ga.resetBottomPanel();
				ga.addMessage("You sent out " + stc.allPokemon.get(0).getName() + "!");
				
				changeBottomPanel++;
				ga.enableCancel(false);
			}
			else if (stc.action == 4) {
				//Opposing player used a move and did not faint your Pokemon
				//damageTaken == -1 means that the move missed
				if (stc.damageTaken != -1) {
					ga.setAllPokemon(stc.allPokemon);
					ga.resetBottomPanel();
					
					int percentDamage = (stc.damageTaken * 100) / stc.allPokemon.get(0).getMaxHP();
					
					String message1 = "The opposing " + stc.opposingPokemonName + " used " + stc.attackName + "!";
					String message2 = stc.allPokemon.get(0).getName() + " lost " + percentDamage 
							+ "% of its health!";				
					
					ga.addMessage(message1);
					ga.addMessage(message2);
				}
				else {					
					String message1 = "The opposing " + stc.opposingPokemonName + " used " + stc.attackName + "!";
					String message2 = "The attack missed!";
					
					ga.addMessage(message1);		
					ga.addMessage(message2);
				}
								
				changeBottomPanel++;
				ga.enableCancel(false);
			}
			else if (stc.action == 5) {
				//You used a move and did not faint opposing Pokemon
				//damageTaken == -1 means that the move missed
				
				if (stc.damageTaken != -1) {
					ga.setOpposingPokemonCurrentHP (stc.opposingCurrentHP);
					
					int percentDamage = stc.damageTaken * 100 / stc.opposingMaxHP;
					
					String message1 = stc.allPokemon.get(0).getName() + " used " + stc.attackName + "!";
					String message2 = "The opposing " + stc.opposingPokemonName + " lost " + percentDamage 
							+ "% of its health!";	
					
					
					ga.addMessage(message1);
					ga.addMessage(message2);
				}
				else {
					String message1 = stc.allPokemon.get(0).getName() + " used " + stc.attackName + "!";
					String message2 = "The attack missed!";
					
					ga.addMessage(message1);
					ga.addMessage(message2);
				}
				
				changeBottomPanel++;
				ga.enableCancel(false);
			}
			else if (stc.action == 6) {
				//Opposing player used a move and fainted your Pokemon
				//And you still have unfainted Pokemon
				
				ga.crossoutFaintedPokemon();
				
				ga.setAllPokemon(stc.allPokemon);
				ga.resetBottomPanel();
				
				int percentDamage = (stc.damageTaken * 100) / stc.allPokemon.get(0).getMaxHP();
				
				String message1 = "The opposing " + stc.opposingPokemonName + " used " + stc.attackName + "!";
				String message2 = stc.allPokemon.get(0).getName() + " lost " + percentDamage 
						+ "% of its health!";				
				String message3 = stc.allPokemon.get(0).getName() + " fainted!";
				
				ga.addMessage(message1);
				ga.addMessage(message2);
				ga.addMessage(message3);
				
				ga.changeBottomPanel (3);
				changeBottomPanel = 1;
			}
			else if (stc.action == 7) {
				//You used a move and fainted the opposing Pokemon
				//And opponent still has unfainted Pokemon
				
				ga.crossoutOpposingFaintedPokemon();

				ga.setOpposingPokemonCurrentHP (stc.opposingCurrentHP);
				
				int percentDamage = stc.damageTaken * 100 / stc.opposingMaxHP;
				
				String message1 = stc.allPokemon.get(0).getName() + " used " + stc.attackName + "!";
				String message2 = "The opposing " + stc.opposingPokemonName + " lost " + percentDamage 
						+ "% of its health!";	
				String message3 = "The opposing " + stc.opposingPokemonName + " fainted!";
				
				ga.addMessage(message1);
				ga.addMessage(message2);
				ga.addMessage(message3);

				ga.changeBottomPanel(2);
				
				changeBottomPanel = 1;
			}
			else if (stc.action == 8) {
				//You lose
				
				ga.crossoutFaintedPokemon();
				
				ga.setAllPokemon(stc.allPokemon);
				ga.resetBottomPanel();
				
				int percentDamage = (stc.damageTaken * 100) / stc.allPokemon.get(0).getMaxHP();
				
				String message1 = "The opposing " + stc.opposingPokemonName + " used " + stc.attackName + "!";
				String message2 = stc.allPokemon.get(0).getName() + " lost " + percentDamage 
						+ "% of its health!";				
				String message3 = stc.allPokemon.get(0).getName() + " fainted!";
				
				ga.addMessage(message1);
				ga.addMessage(message2);
				ga.addMessage(message3);
				ga.addMessage("You lose!");
				
				ga.won(false);
			}
			else {  //stc.action == 9
				//You win
				
				ga.crossoutOpposingFaintedPokemon();

				ga.setOpposingPokemonCurrentHP (stc.opposingCurrentHP);
				
				int percentDamage = stc.damageTaken * 100 / stc.opposingMaxHP;
				
				String message1 = stc.allPokemon.get(0).getName() + " used " + stc.attackName + "!";
				String message2 = "The opposing " + stc.opposingPokemonName + " lost " + percentDamage 
						+ "% of its health!";	
				String message3 = "The opposing " + stc.opposingPokemonName + " fainted!";
				
				ga.addMessage(message1);
				ga.addMessage(message2);
				ga.addMessage(message3);
				ga.addMessage("You win!");
				
				ga.won(true);
			}
			
			
			if (changeBottomPanel >= 2) {
				ga.changeBottomPanel(1);
				ga.enableCancel(true);
				changeBottomPanel = 0;
			}
		}
	}
}