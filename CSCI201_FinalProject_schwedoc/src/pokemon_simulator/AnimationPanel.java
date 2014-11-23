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
		g.setFont(new Font ("Arial", Font.BOLD, 16));
		g.drawString(playerName, 20, 260);
		
		Image pokemonImage = ga.getCurrentPokemonImage();
		String pokemonName = ga.getPokemonName();
		g.drawImage(pokemonImage, 150, 250, this);
		g.setFont(g.getFont().deriveFont((float) 25));
		g.drawString(pokemonName, 150, 200);
		g.drawRect(150, 210, 151, 15);
		g.setColor(Color.GREEN);
		g.fillRect(151, 211, 150 * ga.getCurrentHP() / ga.getMaxHP(), 13);
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
		g.setColor(Color.GREEN);
		g.fillRect(451, 61, 150 * ga.getOpposingCurrentHP() / ga.getOpposingMaxHP(), 13);
		g.setColor(Color.BLACK);
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
