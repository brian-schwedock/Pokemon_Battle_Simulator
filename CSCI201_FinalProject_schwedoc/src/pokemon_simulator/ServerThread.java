package pokemon_simulator;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerThread extends Thread {
	private Server server;
	private ObjectInputStream ois;
	private int playerNumber;
	
	ServerThread(Server server, ObjectInputStream ois, int playerNumber){
		this.server = server;
		this.ois = ois;
		this.playerNumber = playerNumber;
	}
	
	public void run(){
		while (true) {
			try {
				ClientToServer cts = (ClientToServer) ois.readObject();
				if(cts.action == 1)
				{
					System.out.println("Message Received by server thread: " + playerNumber);
					if(playerNumber == 1){
						server.sendMessageToPlayerTwo(cts);
					}else if (playerNumber == 2){
						server.sendMessageToPlayerOne(cts);
					}
					//Chat Message
				}
				else if(cts.action == 2)
				{
					//Move
				}
				else if(cts.action == 3)
				{
					//Switch Pokemon
					server.switchPokemon(cts.pokemonChosen - 1, playerNumber);
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}