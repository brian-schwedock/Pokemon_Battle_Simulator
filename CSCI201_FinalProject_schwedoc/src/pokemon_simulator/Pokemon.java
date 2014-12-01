/*
 * Team members: Brian Schwedock, Ryan Chen,
 * Allen Shi, Chris Holmes, Jonathan Luu, and Alejandro Lopez
 */

/**
 * Pokemon contains all information pertinent to one particular Pokemon
 */

package pokemon_simulator;

import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class Pokemon implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String type;
	private int currentHP;
	private int maxHP;
	private Map<String, Integer> allStats = new HashMap<String, Integer>();
	transient private Image frontImage;
	transient private Image backImage;
	ArrayList<Move> allMoves;
	
	public Pokemon (String name, String type, int HP, int atk,
			int def, int spAtk, int spDef, int spd) {
		this.name = name;
		this.type = type;
		this.currentHP = HP;
		this.maxHP = currentHP;
		
		allStats.put("Attack", atk);
		allStats.put("Defense", def);
		allStats.put("SpecialAttack", spAtk);
		allStats.put("SpecialDefense", spDef);
		allStats.put("Speed", spd);
	}
	
	public void setImages () {
		frontImage = (new ImageIcon ("images/frontSprites/" + name + ".gif")).getImage();
		backImage = (new ImageIcon ("images/backSprites/" + name + ".gif")).getImage();
	}
	
	public void setMoves (ArrayList<Move> inMoves) {
		allMoves = inMoves;
	}
	
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
	
	public void setCurrentHP (int newHP) {
		currentHP = newHP;
		if(currentHP<=0) {
			currentHP=0;
		}
	}
	
	public int getCurrentHP () {
		return currentHP;
	}
	
	public int getMaxHP () {
		return maxHP;
	}
	
	public boolean isFainted () {
		if(currentHP == 0)
			return true;
		return false;
	}
	
	public void printAllStats () {
		System.out.println("Name:" + name + "   Type:" + type + "   HP:" + maxHP);
		System.out.println("Attack:" + allStats.get("Attack") + "   Defense:" + allStats.get("Defense"));
		System.out.print("Sp. Attack:" + allStats.get("SpecialAttack") + "   Special Defense" + allStats.get("SpecialDefense"));
		System.out.println("   Speed:" + allStats.get("Speed"));
		
		for (int i=0; i<4; i++)
			allMoves.get(i).printAllStats();
		
		System.out.println("----------------------------------------------------------------------------");
	}
}
