package edu.ding.eng;

import java.awt.Color;
import java.security.SecureRandom;


public class Country {

	private int territories, GDP, soldiers, technology, population, treasury, growthThreshold;
	private Country countries[];
	private double populationGrowth;
	private Leader leader;
	private Location loc[];
	private AIPriority priority;
	private boolean established = false;
	private int nationID;
	private World world;
	private Color color;

	public Country(int GDP, int soldiers, double populationGrowth, int population, Location loc, Leader leader, int nationID, Color color)
	{
		this.nationID = nationID;
		this.color = color;
		this.GDP = GDP;
		this.soldiers = soldiers;
		this.populationGrowth = populationGrowth;
		this.population = population;
		this.loc = new Location[1];
		territories++;
		this.loc[0] = loc;
		this.loc[0].setColor(color);
		this.leader = leader;

		technology = 0;
		treasury = 0;

		priority = calculateStartingPriority();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	private AIPriority calculateStartingPriority(){
		AIPriority startingPriority;

		if(leader.getIntelligence() > 4){
			startingPriority = new AIPriority(leader, this);
		}
		else{
			startingPriority = new AIPriority(leader, this);
		}
		return startingPriority;
	}

	public void develop()
	{
		if(!established){
			firstTimeSetup();
		}
		GDP += populationGrowth * GDP;
		treasury += GDP;
		population += population * populationGrowth;

		//TODO readd expansion to incorporate the land

	}

	public void expandLocation(Location oldLocs[], Location newLoc){
		Location temp[];
		temp = oldLocs;
		territories++;
		loc = new Location[territories];
		for(int i = 0; i < temp.length; i++){
			loc[i] = temp[i];
		}
		loc[loc.length - 1] = newLoc;
	}

	private void firstTimeSetup() {
		growthThreshold = population + 10;
		established = true;
	}

	public void makeDecisions()
	{
		if(priority.isSharesBorder()){
			System.out.println("ATTACKING");
			if(soldiers > 0){
				world.battle(this, world.getCountries()[priority.getEnemy().getNationID()]);
			}
			else{
				priority.setSharesBorder(false);
			}
		}
	}

	public int getGDP()
	{
		return GDP;
	}

	public int getSoldiers()
	{
		return soldiers;
	}

	public void setSoldiers(int soldiers)
	{
		this.soldiers = soldiers;
	}

	public double getPopulationGrowth()
	{
		return populationGrowth;
	}

	public int getTechnology()
	{
		return technology;
	}

	public int getPopulation()
	{
		return population;
	}

	public int getNationID() {
		return nationID;
	}

	public void setNationID(int nationID) {
		this.nationID = nationID;
	}

	public Leader getLeader() {
		return leader;
	}

	public void setLeader(Leader leader) {
		this.leader = leader;
	}

	public void updateArray(Country[] countries, World w) 
	{
		this.countries = countries;
		this.world = w;
	}

	public Location[] getLocation() 
	{
		return loc;
	}

	public Location getLocation(int index) 
	{
		return loc[index];
	}

	public void setLocation(int index, Location loc) 
	{
		this.loc[index] = loc;
	}

	public void setLocation(Location[] loc) 
	{
		this.loc = loc;
	}

	public int getTerritories() {
		return territories;
	}

	public void setTerritories(int territories) {
		this.territories = territories;
	}

	public AIPriority getPriority() {
		return priority;
	}

	public void setPriority(AIPriority priority) {
		this.priority = priority;
	}

	public void forgetCountry()
	{

	}

}
