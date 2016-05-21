import java.awt.Color;
import java.security.SecureRandom;


public class World implements Runnable {

	private Country[] countries;
	private int numberOfCountries, X, Y;
	public int getX() {
		return X;
	}

	public void setX(int x) {
		X = x;
	}

	public int getY() {
		return Y;
	}

	public void setY(int y) {
		Y = y;
	}

	private Location[] locations;
	public Location[] getLocations() {
		return locations;
	}

	public void setLocations(Location[] locations) {
		this.locations = locations;
	}

	public Country[] getCountries() {
		return countries;
	}

	public void setCountries(Country[] countries) {
		this.countries = countries;
	}

	private SecureRandom r;
	private Map m;

	public World(Map m, int countriesNum, int X, int Y)
	{
		this.m = m;
		numberOfCountries = countriesNum;
		countries = new Country[numberOfCountries];
		this.X = X;
		this.Y = Y;
		locations = new Location[X * Y];
		build();
	}

	private void build()
	{
		assignLocations();
		generateCountries();
	}

	private void assignLocations()
	{
		for(int y = 0; y < Y; y++){
			for(int x = 0; x < X; x++){
				int index = x + y * X;
				System.out.println(X * Y);
				System.out.println(index);
				locations[index] = new Location(x, y);
			}
		}
	}

	public void battle(Country attacker, Country defender){
		System.out.println("IN THE B METHOD");
		int attackerForce = attacker.getSoldiers() * 10; //TODO temp
		int defenderForce = defender.getSoldiers() * (defender.getLeader().getIntelligence());
		System.out.println(attackerForce);
		System.out.println(defenderForce);
		int result = attackerForce - defenderForce;
		if(result > 0){ //attackers win
			attacker.setSoldiers(result / 10);
			defender.setSoldiers(0);
			for(int i = 0; i < defender.getLocation().length; i++){
				System.out.println("for loop tried");
				defender.getLocation()[i].setOwnerID(attacker.getNationID());
				attacker.expandLocation(attacker.getLocation(), defender.getLocation()[i]);
				attacker.getPriority().setSharesBorder(false);
				
				System.out.println("for loop completed");
			}
			defender.setTerritories(0);
			defender.setLocation(null);
			countries[defender.getNationID()] = null;
			System.out.println("attackers win");
		}
		else if(result < 0){ //defenders win
			attacker.setSoldiers(0);
			defender.setSoldiers(result / defender.getLeader().getIntelligence());
			System.out.println("defenders win");
		}
		else{ //still defenders win
			attacker.setSoldiers(0);
			defender.setSoldiers(0);
			System.out.println("stalemate");
		}
	}

	private void generateCountries()
	{
		r = new SecureRandom();
		for(int x = 0; x < numberOfCountries; x++) {
			int l = r.nextInt(X*Y);
			while(locations[l].isOccupied()){
				l = r.nextInt(X*Y);
				System.out.println("The random number:" + l);
			}
			locations[l].setOccupied(true);
			locations[l].setOwnerID(x);
			countries[x] = new Country(X*Y, generateGDP(), generateSoldiers(), generatePopulationGrowth(), generatePopulation(), locations[l], generateLeader(), x, new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
		}
	}

	public int generateGDP()
	{
		return 1000000;
	}

	public int generateSoldiers()
	{
		return 10000;
	}

	public double generatePopulationGrowth()
	{
		return .09;
	}

	public int generateTechnology()
	{
		return 1;
	}

	public int generatePopulation()
	{
		return 100;
	}

	public Leader generateLeader()
	{
		r = new SecureRandom();
		return new Leader(r.nextInt(10), r.nextInt(10), r.nextInt(10));
	}

	private void wipeCountry()
	{

	}

	public void process() 
	{
		for(int i = 0; i < countries.length; i++){
			if(countries[i] != null){
				countries[i].updateArray(countries, this);
			}
		}
		System.out.println("updated");
		for(int i = 0; i < countries.length; i++){
			if(countries[i] != null){
				countries[i].develop();
			}
		}
		System.out.println("developed");
		for(int i = 0; i < countries.length; i++){
			if(countries[i] != null){
				countries[i].makeDecisions();
			}
		}	
		System.out.println("decided");
		Location countryLocations[][] = new Location[countries.length][];
		for(int i = 0; i < countries.length; i++){
			if(countries[i] != null){
				countryLocations[i] = countries[i].getLocation();	
				System.out.println("length of location array: " + countries[i].getLocation().length);
			}
		}
		m.updateLocations(locations, countryLocations);
		System.out.println("updated+");
	}

	@Override
	public void run()
	{
		System.out.println("Successful start");
		process();
		System.out.println("Successful end");
	}

}
