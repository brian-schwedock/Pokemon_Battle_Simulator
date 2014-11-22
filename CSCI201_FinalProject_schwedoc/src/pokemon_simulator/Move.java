package pokemon_simulator;

public class Move {
	private String name;
	//Design doc said "type" is int, but in Pokemon class is String
	private String type;
	private int attack;
	private int specialAttack;
	private int accuracy;
	
	public Move (String name, String type, int attack,
			int specialAttack, int accuracy) {
		this.name = name;
		this.type = type;
		this.attack = attack;
		this.specialAttack = specialAttack;
		this.accuracy = accuracy;
	}
	
	public String getName () {
		return name;
	}
	
	public String getType () {
		return type;
	}
	
	public int getAttack () {
		return attack;
	}
	
	public int getSpecialAttack () {
		return specialAttack;
	}
	
	public int getAccuracy () {
		return accuracy;
	}
}
