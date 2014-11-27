package pokemon_simulator;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerThread extends Thread {
	private Server server;
	private ObjectInputStream ois;
	private int playerNumber;
	private boolean moveMade;
	
	ServerThread(Server server, ObjectInputStream ois, int playerNumber){
		this.server = server;
		this.ois = ois;
		this.playerNumber = playerNumber;
		moveMade = false;
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
				}else{
					// this occurs when actions 2, 3 or 4 are chosen. the server class
					// will handle appropriate implementation of these instances
					if(cts.action != 4){
						if(playerNumber == 1 && !server.playerOneMadeMove){
							server.setCTS(true, cts);
							server.playerOneMadeMove = true;
							server.incrementActionCount();
						}
						if(playerNumber == 2 && !server.playerTwoMadeMove){
							server.setCTS(false, cts);
							server.playerTwoMadeMove = true;
							server.incrementActionCount();
						}
					}
					else {  //Chose new Pokemon after fainting
						if (playerNumber == 1){
							server.setCTS(true, cts);
							server.playerOneMadeMove = true;
							server.playerTwoMadeMove = false;
						}
						else{
							server.setCTS(false, cts);
							server.playerOneMadeMove = false;
							server.playerTwoMadeMove = true;
						}
						server.makePlayerMoves();
					}
					
					if(server.getActionCount() == 2){
						server.makePlayerMoves();
					}
					
				}
			} catch (Exception e)
			{
				System.exit(0);
			}
		}
	}
}