package pokemon_simulator;
//Allen test commit
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class AnimationPanel extends JPanel {
	GameApplication ga;
	ImageIcon playerImage;
	ImageIcon opposingPlayerImage;
	ImageIcon pokeballImage;
	
	public AnimationPanel (GameApplication ga, ImageIcon playerImage,
			ImageIcon opposingPlayerImage) {
		this.ga = ga;
		this.playerImage = playerImage;
		this.opposingPlayerImage = opposingPlayerImage;
		//pokeballImage = 
		
	}
	
	protected void paintComponent (Graphics g) {
		super.paintComponent(g);
		
		g.setFont(new Font (g.getFont().getName(), g.getFont().getStyle(), 50));
		g.drawString("Imagine two Pokemon here", 100, 200);
	}
}
