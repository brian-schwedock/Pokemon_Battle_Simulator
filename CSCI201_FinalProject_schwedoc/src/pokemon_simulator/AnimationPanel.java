package pokemon_simulator;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class AnimationPanel extends JPanel {
	GameApplication ga;
	ImageIcon playerImage;
	ImageIcon opposingPlayerImage;
	Image pokeballImage;
	
	public AnimationPanel (GameApplication ga, ImageIcon playerImage,
			ImageIcon opposingPlayerImage) {
		this.ga = ga;
		this.playerImage = playerImage;
		this.opposingPlayerImage = opposingPlayerImage;
		
		//Get the pokeballImage and resize it
		pokeballImage = (new ImageIcon ("images/pokeball.png")).getImage();
	}
	
	protected void paintComponent (Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(pokeballImage, 50, 50, 20, 20, null);
	}
}
