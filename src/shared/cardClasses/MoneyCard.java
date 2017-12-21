package shared.cardClasses;
/**
 * 
 * @author Mario Allemann
 *
 */
public class MoneyCard extends Card{

	// Serializable default
	private static final long serialVersionUID = 1L;
	int value;
	


	public MoneyCard(String name, int cost, int value) {
		super(name, cost);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
	@Override
	public String toString() {
		return "MoneyCard [value=" + value + ", name=" + name + ", cost=" + cost + "]";
	}
}
