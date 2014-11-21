package pokemon_simulator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class AnimationPanel extends JPanel {
	GameApplication ga;
	Image playerImage;
	Image opposingPlayerImage;
	Image pokeballImage;
	Image pokemonImage;
	
	public AnimationPanel (GameApplication ga, Image playerImage,
			Image opposingPlayerImage) {
		this.ga = ga;
		this.playerImage = playerImage;
		this.opposingPlayerImage = opposingPlayerImage;
		
		pokeballImage = (new ImageIcon ("images/pokeball.png")).getImage();
		
		//pokemonImage = (new ImageIcon ("images/frontSprites/Mewtwo.gif")).getImage();
	}
	
	protected void paintComponent (Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(pokemonImage, 150, 150, this);
	}
	
	public void crossOutPokemon (Graphics g, int whichPokemon) {
		//TODO: write function
	}
	
	public void crossOutOpposingPokemon (Graphics g, int numberOfPokemon) {
		//TODO: write function
	}
	
	public void drawHP (Graphics g, int currentHP, int maxHP) {
		//TODO: write function
	}
	
	public void drawOpposingHP (Graphics g, int opposingCurrentHP, int opposingMaxHP) {
		//TODO: write function
	}
}
