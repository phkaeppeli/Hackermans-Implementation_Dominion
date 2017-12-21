package shared;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Stack;

import shared.cardClasses.Card;
import shared.resourceClasses.StackType;

/**
 * 
 * @author Mario Allemann
 *
 */
public class CardStack implements Serializable{

	// Serializable default
	private static final long serialVersionUID = 1L;
	protected StackType type;
	// If the stack contains mixed cards, store them in a stack
	protected Stack<Card> cards;


	public CardStack(StackType type) {
		this.type = type;
		this.cards = new Stack<Card>();
	}

	public CardStack(StackType type, Stack<Card> cards) {
		this.type = type;
		this.cards = cards;
	}



	public void addCard(Card card) {
		this.cards.push(card);
	}

	public Card removeCard() {
		return this.cards.pop();
	}

	public int size() {
		return cards.size();
	}

	public Stack<Card> getStack() {
		return cards;
	}


	@Override
	public String toString() {
		String repr = type.toString() + " size: " + size();
		Iterator<Card> iter = cards.iterator();
		while (iter.hasNext()) {
			repr += "\n" + iter.next().getName();
		}

		return repr;
	}

}
