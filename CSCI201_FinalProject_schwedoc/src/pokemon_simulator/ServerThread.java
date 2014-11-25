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
					// this occurs when actions 2 or three are chosen. the server class
					// will handle appropriate implementation of these instances
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
					
					
					if(server.getActionCount() >=2){
						server.makePlayerMoves();
						
					}
					
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