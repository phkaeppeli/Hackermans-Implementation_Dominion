package shared.cardClasses;

/**
 * 
 * @author Mario Allemann
 *
 */
public class ActionCard extends Card {

	// Serializable default
	private static final long serialVersionUID = 1L;
	int plusAction;
	int plusDraw;
	int plusMoney;
	int plusBuy;
	int specialActionID;

	public ActionCard(String name, int cost, int plusAction, int plusCard, int plusMoney, int plusBuy,
			int specialActionID) {
		super(name, cost);
		this.plusAction = plusAction;
		this.plusDraw = plusCard;
		this.plusMoney = plusMoney;
		this.plusBuy = plusBuy;
		this.specialActionID = specialActionID;
	}
	
	
	public int getPlusAction() {
		return plusAction;
	}


	public int getPlusDraw() {
		return plusDraw;
	}


	public int getPlusMoney() {
		return plusMoney;
	}


	public int getPlusBuy() {
		return plusBuy;
	}


	public int getSpecialActionID() {
		return specialActionID;
	}


	@Override
	public String toString() {
		return "ActionCard [plusAction=" + plusAction + ", plusCard=" + plusDraw + ", plusMoney=" + plusMoney
				+ ", plusBuy=" + plusBuy + ", specialActionID=" + specialActionID + ", name=" + name + ", cost=" + cost
				+ "]";
	}

}
