package pokemon_simulator;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.sound.sampled.*;

public class LoopSound implements Runnable{
	public void run(){
		try{
	        File file = new File(
	            "Sound\\trainer-battle.mid");
	        Clip clip;
			try {
				clip = AudioSystem.getClip();
				 // getAudioInputtream() also accepts a File or InputStream
		        AudioInputStream ais=null;
				try {
					ais = AudioSystem.
					    getAudioInputStream( file );
				} catch (UnsupportedAudioFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        clip.open(ais);
		        clip.loop(Clip.LOOP_CONTINUOUSLY);
		       
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       return;
    	}
    	catch (IOException e) {
    		    throw new RuntimeException(e);
    	}
	}
	
}
