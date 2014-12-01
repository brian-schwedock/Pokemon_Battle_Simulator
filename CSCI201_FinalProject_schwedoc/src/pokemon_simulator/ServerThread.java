/*
 * Team members: Brian Schwedock, Ryan Chen,
 * Allen Shi, Chris Holmes, Jonathan Luu, and Alejandro Lopez
 */

/**
 *The job of this class is to listen to messages (ClientToServer)
 *and call the corresponding functions in Server
 *
 *This class will never send information to the GameApplication
 *Server will be the only class to send information to the GameApplication
*/

package pokemon_simulator;

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
				if (cts.action == 1) {
					//Chat Message
					if (playerNumber == 1) {
						server.sendMessageToPlayerTwo(cts);
					} else if (playerNumber == 2) {
						server.sendMessageToPlayerOne(cts);
					}
				} else {
					// this occurs when actions 2, 3 or 4 are chosen. the server class
					// will handle appropriate implementation of these instances
					if (cts.action != 4 && cts.action != 5) {
						if (playerNumber == 1 && !server.playerOneMadeMove) {
							server.setCTS(true, cts);
							server.playerOneMadeMove = true;
							server.incrementActionCount();
						}
						if (playerNumber == 2 && !server.playerTwoMadeMove) {
							server.setCTS(false, cts);
							server.playerTwoMadeMove = true;
							server.incrementActionCount();
						}
					}
					else if (cts.action == 4) {  //Chose new Pokemon after fainting
						if (playerNumber == 1) {
							server.setCTS(true, cts);
							server.playerOneMadeMove = true;
							server.playerTwoMadeMove = false;
						}
						else {
							server.setCTS(false, cts);
							server.playerOneMadeMove = false;
							server.playerTwoMadeMove = true;
						}
						server.makePlayerMoves();
					}
					else { //action was cancelled
						server.decrementActionCount();
						
						if (playerNumber == 1)
							server.playerOneMadeMove = false;
						else
							server.playerTwoMadeMove = false;
					}
					
					if (server.getActionCount() == 2) {
						server.makePlayerMoves();
					}
					
				}
			} 
			catch (Exception e) {
				System.exit(0);
			}
		}
	}
}