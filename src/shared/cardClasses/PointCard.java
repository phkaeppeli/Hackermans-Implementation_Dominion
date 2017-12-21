package shared.cardClasses;

/**
 * 
 * @author Mario Allemann
 *
 */
public class PointCard extends Card {

	// Serializable default
	private static final long serialVersionUID = 1L;
	int pointValue;

	public PointCard(String name, int cost, int pointValue) {
		super(name, cost);
		this.pointValue = pointValue;
	}
	
	public int getValue() {
		return pointValue;
	}
	
	
	@Override
	public String toString() {
		return "PointCard [pointValue=" + pointValue + ", name=" + name + ", cost=" + cost + "]";
	}


}
