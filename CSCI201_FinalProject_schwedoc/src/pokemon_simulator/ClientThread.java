//Changed name from ServerListener to ClientThread

//The job of this class is to listen to messages (ServerToClient)
//and call the corresponding functions in GameApplication

//This class will never send information to the Server
//GameApplication will be the only class to send information to the Server

package pokemon_simulator;

import java.awt.CardLayout;
import java.io.EOFException;
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
				System.out.println("READ IN OBJECT");
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
				//Opposing player sent a chat message
				
				ga.addChatMessage(stc.message, opposingPlayerName);
			}
			else if (stc.action == 2){
				//Opposing player switched Pokemon
				
				ga.setOpposingPokemonImage(stc.opposingPokemonImage);
				ga.setOpposingPokemonCurrentHP (stc.opposingCurrentHP);
				ga.setOpposingPokemonMaxHP (stc.opposingMaxHP);
				ga.setOpposingPokemonName(stc.opposingPokemonName);
				//ga.repaint();
				
				ga.addMessage(opposingPlayerName + " sent out " + stc.opposingPokemonName + "!");
				
				changeBottomPanel++;
			}
			else if (stc.action == 3){
				//You switched Pokemon
				
				ga.setAllPokemon(stc.allPokemon);
				ga.resetBottomPanel();
				ga.addMessage("You sent out " + stc.allPokemon.get(0).getName() + "!");
				//ga.repaint();
				
				changeBottomPanel++;
			}
			else if (stc.action == 4){
				//Opposing player used a move and did not faint your Pokemon
				//damageTaken == -1 means that the move missed
				if (stc.damageTaken != -1){
					ga.setAllPokemon(stc.allPokemon);
					ga.resetBottomPanel();
					
					int percentDamage = (stc.damageTaken * 100) / stc.allPokemon.get(0).getMaxHP();
					
					String message1 = "The opposing " + stc.opposingPokemonName + " used " + stc.attackName + "!";
					String message2 = stc.allPokemon.get(0).getName() + " lost " + percentDamage 
							+ "% of its health!";				
					
					ga.addMessage(message1);
					ga.addMessage(message2);
				}
				else{					
					String message1 = "The opposing " + stc.opposingPokemonName + " used " + stc.attackName + "!";
					String message2 = "The attack missed!";
					
					ga.addMessage(message1);		
					ga.addMessage(message2);
				}
				
				//ga.repaint();
				
				changeBottomPanel++;
			}
			else if (stc.action == 5){
				//You used a move and did not faint opposing Pokemon
				//damageTaken == -1 means that the move missed
				
				//System.out.println("ACTION 5 DETECTED");
				if (stc.damageTaken != -1){
					ga.setOpposingPokemonCurrentHP (stc.opposingCurrentHP);
					
					int percentDamage = stc.damageTaken * 100 / stc.opposingMaxHP;
					
					String message1 = stc.allPokemon.get(0).getName() + " used " + stc.attackName + "!";
					String message2 = "The opposing " + stc.opposingPokemonName + " lost " + percentDamage 
							+ "% of its health!";	
					
					
					ga.addMessage(message1);
					ga.addMessage(message2);
				}
				else{
					String message1 = stc.allPokemon.get(0).getName() + " used " + stc.attackName + "!";
					String message2 = "The attack missed!";
					
					ga.addMessage(message1);
					ga.addMessage(message2);
				}
				//ga.repaint();
				
				changeBottomPanel++;
			}
			else if (stc.action == 6){
				//Opposing player used a move and fainted your Pokemon
				//And you still have unfainted Pokemon

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
				

				
			}
			else if (stc.action == 7){
				//You used a move and fainted the opposing Pokemon
				//And opponent still has unfainted Pokemon

				ga.setOpposingPokemonCurrentHP (stc.opposingCurrentHP);
				
				int percentDamage = stc.damageTaken * 100 / stc.opposingMaxHP;
				
				String message1 = stc.allPokemon.get(0).getName() + " used " + stc.attackName + "!";
				String message2 = "The opposing " + stc.opposingPokemonName + " lost " + percentDamage 
						+ "% of its health!";	
				String message3 = "The opposing " + stc.opposingPokemonName + " fainted!";
				
				ga.addMessage(message1);
				ga.addMessage(message2);
				ga.addMessage(message3);
				
			}
			else if (stc.action == 8){
				//You lose
				
			}
			else {  //stc.action == 9
				//You win
				
			}
			
			/*
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