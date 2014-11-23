package pokemon_simulator;

import java.io.ObjectInputStream;

public class ServerThread extends Thread {
	private Server server;
	private ObjectInputStream ois;
	
	ServerThread(Server server, ObjectInputStream ois){
		this.server = server;
		this.ois = ois;
	}
	
	public void run(){
		while (true) {
			
		}
	}
}