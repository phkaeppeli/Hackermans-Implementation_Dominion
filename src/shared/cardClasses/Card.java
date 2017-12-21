package shared.cardClasses;

import java.io.Serializable;

/**
 * 
 * @author Mario Allemann
 *
 */
public class Card implements Serializable {

	// Serializable default
	private static final long serialVersionUID = 1L;
	protected String name;
	protected int cost;
	


	public Card(String name, int cost) {
		this.name = name.toLowerCase();
		this.cost = cost;
	}
	


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
	
}
