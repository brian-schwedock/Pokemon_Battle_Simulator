/*
 * Team members: Brian Schwedock, Ryan Chen,
 * Allen Shi, Chris Holmes, Jonathan Luu, and Alejandro Lopez
 */

/**
 * LoopSound contains the code necessary to run background music
 */

package pokemon_simulator;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

public class LoopSound implements Runnable {
	public void run(){
		try{
	        File file = new File (
	            "Sound\\trainer-battle.mid");
	        Clip clip;
			try {
				clip = AudioSystem.getClip();
				 // getAudioInputtream() also accepts a File or InputStream
		        AudioInputStream ais = null;
				try {
					ais = AudioSystem.
					    getAudioInputStream (file);
				} catch (UnsupportedAudioFileException e) {
					//e.printStackTrace();
				}
		        clip.open(ais);
		        
		        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		        
		        //Reduce volume by 20 decibels.
		        gainControl.setValue(-20.0f);
		        
		        clip.loop(Clip.LOOP_CONTINUOUSLY);
		       
			} catch (LineUnavailableException e) {
				//e.printStackTrace();
			}
	       return;
    	}
    	catch (IOException e) {
    		throw new RuntimeException(e);
    	}
	}
	
}
