/*
 * Team members: Brian Schwedock, Ryan Chen,
 * Allen Shi, Chris Holmes, Jonathan Luu, and Alejandro Lopez
 */

/**
 * AnimationPanel is part of the GUI and contains all of the graphics.
 * The Pokemon images and hp, the player images, pokeball images, background
 */

package pokemon_simulator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class AnimationPanel extends JPanel {
	GameApplication ga;
	Image playerImage;
	Image opposingPlayerImage;
	Image pokeballImage;
	Image pokeballXImage;
	Image backgroundImage;
	String playerName;
	String opposingPlayerName;
	Integer currPokemonFainted, opponentPokemonFainted;
	
	public AnimationPanel (GameApplication ga, String playerName, String opposingPlayerName) {
		this.ga = ga;
		this.playerName = playerName;
		this.opposingPlayerName = opposingPlayerName;
		this.playerName = playerName;
		this.opposingPlayerName = opposingPlayerName;
		
		currPokemonFainted = 0;
		opponentPokemonFainted = 0;
		setImages();
	}
	
	protected void paintComponent (Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
		
		//Draw components for player
		g.drawImage(playerImage, 20, 275, 60, 100, this);
		g.setFont(new Font ("Arial", Font.BOLD, 16));
		g.drawString(playerName, 20, 260);
		
		Image pokemonImage = ga.getCurrentPokemonImage();
		String pokemonName = ga.getPokemonName();
		g.drawImage(pokemonImage, 150, 250, this);
		g.setFont(g.getFont().deriveFont((float) 25));
		g.drawString(pokemonName, 150, 200);
		g.drawRect(150, 210, 151, 15);
		drawHP(g);
		g.setColor(Color.BLACK);
		
		//Draw components for opponent
		g.drawImage(opposingPlayerImage, 700, 75, 60, 100, this);
		g.setFont(g.getFont().deriveFont((float) 16));;
		g.drawString(opposingPlayerName, 700, 60);
		
		Image opposingPokemonImage = ga.getOpposingPokemonImage();
		String opposingPokemonName = ga.getOpposingPokemonName();
		g.drawImage(opposingPokemonImage, 450, 100, this);
		g.setFont(g.getFont().deriveFont((float) 25));
		g.drawString(opposingPokemonName, 450, 50);
		g.drawRect(450, 60, 151, 15);
		drawOpposingHP(g);
		g.setColor(Color.BLACK);
		
		for (int i=0; i<2; i++) {
			for (int j=0;  j < 3; j++) {
				if (currPokemonFainted > (i * 3 + j)) {
					//If there are any fainted pokemon, draw an X'd out pokeball
					g.drawImage(pokeballXImage, 24+(j*20), 380+(i*15), 15, 15, this);
					continue;
				}
				g.drawImage(pokeballImage, 24+(j*20), 380+(i*15), 15, 15, this);
			}
		}
		
		for (int i=0; i<2; i++) {
			for (int j=0; j < 3; j++) {
				if (opponentPokemonFainted > i * 3 + j) {
					//If there are any fainted pokemon, draw an X'd out pokeball
					g.drawImage(pokeballXImage, 705+(j*20), 180+(i*15), 15, 15, this);
					continue;
				}
				g.drawImage(pokeballImage, 705+(j*20), 180+(i*15), 15, 15, this);
			}
		}
	}
	
	private void setImages () {
		pokeballImage = (new ImageIcon ("images/pokeball.gif")).getImage();
		pokeballXImage = (new ImageIcon ("images/pokeballX.png")).getImage();
		backgroundImage= new ImageIcon ("images/back.jpg").getImage();
		if (playerName.equals("Player 1")) {
			playerImage = (new ImageIcon ("images/ash.gif")).getImage();
			opposingPlayerImage = (new ImageIcon ("images/gary.gif")).getImage();
		}
		else {
			opposingPlayerImage = (new ImageIcon ("images/ash.gif")).getImage();
			playerImage = (new ImageIcon ("images/gary.gif")).getImage();
		}
	}
	
	public void crossOutPokemon () {
		currPokemonFainted++;
	}
	
	public void crossOutOpposingPokemon () {
		opponentPokemonFainted++; 
	}
	
	public void drawHP (Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect(151, 211, 150 * ga.getCurrentHP() / ga.getMaxHP(), 13);
	}
	
	public void drawOpposingHP (Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect(451, 61, 150 * ga.getOpposingCurrentHP() / ga.getOpposingMaxHP(), 13);
	} 
}
