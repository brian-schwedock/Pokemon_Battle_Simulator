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
	String playerName;
	String opposingPlayerName;
	
	public AnimationPanel (GameApplication ga, String playerName, String opposingPlayerName) {
		this.ga = ga;
		this.playerName = playerName;
		this.opposingPlayerName = opposingPlayerName;
		this.playerName = playerName;
		this.opposingPlayerName = opposingPlayerName;
		
		setImages();
	}
	
	protected void paintComponent (Graphics g) {
		super.paintComponent(g);
		
		//Draw components for player
		g.drawImage(playerImage, 20, 275, 60, 100, this);
		g.drawString(playerName, 30, 260);
		
		Image pokemonImage = ga.getCurrentPokemonImage();
		g.drawImage(pokemonImage, 150, 250, this);
		
		
		//Draw components for opponent
		g.drawImage(opposingPlayerImage, 700, 75, 60, 100, this);
		g.drawString(opposingPlayerName, 710, 60);
		
		Image opposingPokemonImage = ga.getOpposingPokemonImage();
		g.drawImage(opposingPokemonImage, 450, 100, this);
	}
	
	private void setImages () {
		pokeballImage = (new ImageIcon ("images/pokeball.png")).getImage();
		
		if (playerName.equals("Player 1")) {
			playerImage = (new ImageIcon ("images/ash.png")).getImage();
			opposingPlayerImage = (new ImageIcon ("images/gary.gif")).getImage();
		}
		else {
			opposingPlayerImage = (new ImageIcon ("images/ash.png")).getImage();
			playerImage = (new ImageIcon ("images/gary.gif")).getImage();
		}
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
