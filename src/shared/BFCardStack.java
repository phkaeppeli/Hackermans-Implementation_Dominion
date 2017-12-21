package shared;

import java.io.Serializable;

import shared.cardClasses.Card;
import shared.resourceClasses.StackType;

/**
 * All cardstacks which only have a single card on it
 * @author Mario Allemann
 *
 */
public class BFCardStack implements Serializable {

	// Serializable default
	private static final long serialVersionUID = 1L;
	// If the stack contains the same card, it isn't necessary to store every card
	// object, just store the amount (e.g. all stacks on the battlefield)
	protected int size;
	protected Card card;
	//The name of the card the stack represents
	protected String name;
	protected StackType type;
	
	public BFCardStack(StackType type, Card card, int numOfCards) {
		this.type = type;
		this.size = numOfCards;
		this.card = card;
		this.name = card.getName();
	}

	public String getName() {
		return name;
	
	}
	
	public Card getCard() {
		return card;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public Card removeCard() {
		size--;
		return card;
	}
	
	public Card removeCards(int numOfCards) {
		size = size-numOfCards;
		return null;
	}

	public StackType getType() {
		return type;
	}
}
