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

	public Country(int worldSize, int GDP, int soldiers, double populationGrowth, int population, Location loc, Leader leader, int nationID, Color color)
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

		if(population >= growthThreshold){			//expand into a random bordering area
			System.out.println("EXPANDING");

			SecureRandom r = new SecureRandom();
			System.out.println(loc.length);
			int chosenLocation = r.nextInt(loc.length); //TODO change
			int growthX = r.nextInt(3) - 1;
			int growthY = r.nextInt(3) - 1;
			System.out.println(getLocation(chosenLocation).getX() + ", " + getLocation(chosenLocation).getY());
			int tally = 0;
			while(getLocation(chosenLocation).getY() + growthY > world.getY() -1 //when growth is too north/south
					|| getLocation(chosenLocation).getY() + growthY < 0
					|| (world.getLocations()[getLocation(chosenLocation).getX() + world.getX() * (getLocation(chosenLocation).getY() + growthY) + growthX] == null
					||		world.getLocations()[getLocation(chosenLocation).getX() + world.getX() * (getLocation(chosenLocation).getY() + growthY) + growthX].isOccupied())){
				System.out.println("started loop");
				growthX = r.nextInt(3) - 1;
				growthY = r.nextInt(3) - 1;
				tally++;
				System.out.println("tally added");
				if(tally == 8){
					chosenLocation = r.nextInt(loc.length); //TODO create better algorithm for finding expansion tile
					System.out.println(getLocation(chosenLocation).getX() + ", " + getLocation(chosenLocation).getY());
					tally = 0;
				}
			}

			if(getLocation(chosenLocation).getX() + growthX == 1 + world.getX()){ //east/west growth
				int x = -world.getX() + getLocation(chosenLocation).getX();
				System.out.println((getLocation(chosenLocation).getX() + growthX) + ", " + (getLocation(chosenLocation).getY() + growthY));
				System.out.println((x + growthX) + ", " + (getLocation(chosenLocation).getY() + growthY));
				world.getLocations()[x + world.getX() * (getLocation(chosenLocation).getY() + growthY) + growthX].setOccupied(true);
				world.getLocations()[x + world.getX() * (getLocation(chosenLocation).getY() + growthY) + growthX].setOwnerID(nationID);
				expandLocation(loc, world.getLocations()[x + world.getX() * (getLocation(chosenLocation).getY() + growthY) + growthX]);
			}
			else if(getLocation(chosenLocation).getX() + growthX == -1){
				System.out.println("LEFT");
				int x = world.getX() + getLocation(chosenLocation).getX();
				System.out.println("LEFT AGAIN");
				System.out.println((x + growthX) + ", " + (getLocation(chosenLocation).getY() + growthY));
				world.getLocations()[x + world.getX() * (getLocation(chosenLocation).getY() + growthY) + growthX].setOccupied(true);
				world.getLocations()[x + world.getX() * (getLocation(chosenLocation).getY() + growthY) + growthX].setOwnerID(nationID);
				expandLocation(loc, world.getLocations()[x + world.getX() * (getLocation(chosenLocation).getY() + growthY) + growthX]);
			}
			else{
				System.out.println((getLocation(chosenLocation).getX() + growthX) + ", " + (getLocation(chosenLocation).getY() + growthY));
				world.getLocations()[getLocation(chosenLocation).getX() + world.getX() * (getLocation(chosenLocation).getY() + growthY) + growthX].setOccupied(true);
				world.getLocations()[getLocation(chosenLocation).getX() + world.getX() * (getLocation(chosenLocation).getY() + growthY) + growthX].setOwnerID(nationID);
				expandLocation(loc, world.getLocations()[getLocation(chosenLocation).getX() + world.getX() * (getLocation(chosenLocation).getY() + growthY) + growthX]);
			}


			System.out.println("expanded");
			growthThreshold += 10;

			if(soldiers > 0){
				for(int i = 0; i < loc.length; i++){ //Acknowledge bordered country
					for(int y = -1; y < 2; y++){
						for(int x = -1; x < 2; x++){
							if(getLocation(i).getY() + y < world.getY() && getLocation(i).getY() + y > 0){
								Location z = world.getLocations()[getLocation(i).getX() + world.getX() * (getLocation(i).getY() + y) + x];
								if(z.getOwnerID() != nationID && z.getOwnerID() > -1){
									System.out.println("ENEMYYYYYYYYYYYYYYYYYYY");
									priority.setSharesBorder(true);
									priority.setEnemy(world.getCountries()[z.getOwnerID()]);
								}
							}
						}
					}
				}
			}
		}

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
