package server.database;

import shared.cardClasses.Card;

/**
 * A wrapper class, which holds a Card object and its default stack size
 * @author Mario Allemann
 *
 */
public class CardPair {

	private Card card;
	private int stackSize;

	public CardPair(Card card, int stackSize) {
		super();
		this.card = card;
		this.stackSize = stackSize;
	}

	public Card getCard() {
		return card;
	}

	public int getStackSize() {
		return stackSize;
	}

}