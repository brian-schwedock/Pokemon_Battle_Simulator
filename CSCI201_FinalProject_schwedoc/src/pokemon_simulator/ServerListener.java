package pokemon_simulator;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerListener extends Thread {
	ObjectInputStream ois;
	GameApplication ga;
	
	public ServerListener (ObjectInputStream ois, GameApplication ga) {
		this.ois = ois;
		this.ga = ga;
	}
	
	public void run () {
		try {
			ServerToClient stc = (ServerToClient) ois.readObject ();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
