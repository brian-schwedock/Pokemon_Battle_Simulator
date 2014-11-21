package pokemon_simulator;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.ImageIcon;

public class Pokemon {
	private String name;
	private String type;
	//private int ID  -- Are we using this?
	private int currentHP;
	private int maxHP;
	private Map<String, Integer> allStats;
	private Image frontImage;
	private Image backImage;
	ArrayList<Move> allMoves;
	
	public Pokemon (String name, String type, int HP, int atk,
			int def, int spAtk, int spDef, int spd) {
		this.name = name;
		this.type = type;
		this.maxHP = this.currentHP = HP;
		//Set stats
		//setMoves();
		setImages();
	}
	
	private void setImages () {
		frontImage = (new ImageIcon ("images/frontSprites/" + name + ".gif")).getImage();
		backImage = (new ImageIcon ("images/backSprites/" + name + ".gif")).getImage();
	}
	
	private void setMoves () {
		//TODO: write the function
	}
	
	/*
	 public int getID () {
	 }
	 */
	
	public String getName () {
		return name;
	}
	
	public Image getFrontImage () {
		return frontImage;
	}
	
	public Image getBackImage () {
		return backImage;
	}
	
	public Map<String, Integer> getAllStats () {
		return allStats;
	}
	
	public ArrayList<Move> getMoves () {
		return allMoves;
	}
	
	public String getType () {
		return type;
	}
	
	public int getCurrentHP () {
		return currentHP;
	}
	
	public int getMaxHP () {
		return maxHP;
	}
}
