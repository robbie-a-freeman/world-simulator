
public class AIPriority{
	
	private Leader leader;
	private Country c;
	private boolean sharesBorder = false;
	private Country enemy = null;

	public AIPriority(Leader leader, Country c){
		this.leader = leader;
		this.c = c;
	}
	
	public boolean isSharesBorder() {
		return sharesBorder;
	}

	public void setSharesBorder(boolean sharesBorder) {
		this.sharesBorder = sharesBorder;
	}

	public Country getEnemy() {
		return enemy;
	}

	public void setEnemy(Country enemy) {
		this.enemy = enemy;
	}

}
