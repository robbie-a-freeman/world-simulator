package edu.ding.eng;


public class Leader {

	private int intelligence, persuasion, compassion;
	
	public Leader(int intelligence, int persuasion, int compassion)
	{
		this.setIntelligence(intelligence);
		this.setPersuasion(persuasion);
		this.setCompassion(compassion);
		System.out.println(intelligence + " " + persuasion + " " + compassion);
	}

	public int getIntelligence() {
		return intelligence;
	}

	public void setIntelligence(int intelligence) {
		this.intelligence = intelligence;
	}

	public int getPersuasion() {
		return persuasion;
	}

	public void setPersuasion(int persuasion) {
		this.persuasion = persuasion;
	}

	public int getCompassion() {
		return compassion;
	}

	public void setCompassion(int compassion) {
		this.compassion = compassion;
	}
	
	
	
}