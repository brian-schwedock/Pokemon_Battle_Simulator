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
			
		}
	}
}