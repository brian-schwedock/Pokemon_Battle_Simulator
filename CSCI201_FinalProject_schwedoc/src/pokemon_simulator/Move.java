package pokemon_simulator;

import java.io.Serializable;

public class Move implements Serializable{
	private String name;
	private String type;
	private int power;
	private boolean attackType;
	private int accuracy;
	
	public Move (String name, String type, int attack,
			boolean isSpecial, int accuracy) {
		this.name = name;
		this.type = type;
		this.power = attack;
		this.attackType = isSpecial;
		this.accuracy = accuracy;
	}
	
	public String getName () {
		return name;
	}
	
	public String getType () {
		return type;
	}
	
	public int getAttackPower () {
		return power;
	}
	
	public boolean isSpecial () {
		return attackType;
	}
	
	public int getAccuracy () {
		return accuracy;
	}
	
	public void printAllStats(){
		System.out.print("Move Name:" + name + " Type:" + type);
		System.out.println(" Attack power:" + power + " Physical/Special (0/1):" + attackType + " Accuracy:" + accuracy);
	}
}
