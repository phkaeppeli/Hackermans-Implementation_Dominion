package server.resourceClasses;

import java.util.ArrayList;

import server.database.DataGrabber;
import server.database.CardPair;
import shared.BFCardStack;
import shared.cardClasses.Card;
import shared.resourceClasses.StackType;

/**
 * Represents all the cards on the battlefield
 * 
 * @author Mario Allemann
 *
 */
public class Battlefield {

	protected ArrayList<BFCardStack> allStacks;

	/**
	 * Creates a new Battlefield
	 * 
	 * @param actionCardNames
	 *            The action cards which got selected by the lobby leader beforehand
	 * 
	 * @author Mario Allemann
	 */
	public Battlefield(ArrayList<String> actionCardNames) {
		allStacks = new ArrayList<BFCardStack>();
		DataGrabber dg = new DataGrabber();

		// Adds the point cards to the battlefield
		for (CardPair cp : dg.getPointCards()) {
			allStacks.add(new BFCardStack(StackType.POINT, cp.getCard(), cp.getStackSize()));
		}

		// Adds all action cards to the battlefield (which got selected beforehand)
		for (CardPair cp : dg.getActionCards()) {
			if (actionCardNames.contains(cp.getCard().getName())) {
				allStacks.add(new BFCardStack(StackType.ACTION, cp.getCard(), cp.getStackSize()));
			}

		}
		// Adds all money cards to the battlefield
		for (CardPair cp : dg.getMoneyCards()) {
			allStacks.add(new BFCardStack(StackType.MONEY, cp.getCard(), cp.getStackSize()));
		}
	}

	/**
	 * Draws a card from a battlefield stack
	 * 
	 * @param cardName
	 *            The name of the desired card
	 * @return The card object which got drawn
	 * 
	 * @author Mario Allemann
	 */
	public Card drawCardFromStack(String cardName) {
		for (BFCardStack cs : allStacks) {
			if (cs.getName().equals(cardName)) {
				return cs.removeCard();
			}
		}
		return null;
	}

	/**
	 * Looks at a card from the battlefield without removing it
	 * 
	 * @param cardName
	 *            The name of the desired card
	 * @return The card object which was looked at
	 * 
	 * @author Mario Allemann
	 */
	public Card peekCard(String cardName) {
		for (BFCardStack cs : allStacks) {
			if (cs.getName().equals(cardName)) {
				return cs.getCard();
			}
		}
		return null;
	}

	/**
	 * Gets a stack from the battlefield
	 * 
	 * @param cardName
	 *            The name of the stack
	 * @return The desired BFCardStack object
	 * 
	 * @author Mario Allemann
	 */
	public BFCardStack getCardStack(String cardName) {
		for (BFCardStack cs : allStacks) {
			if (cs.getName().equals(cardName)) {
				return cs;
			}
		}
		return null;
	}

	/**
	 * Sets the size of the battlefield stack
	 * 
	 * @param cardName
	 *            The desired stack
	 * @param size
	 *            The desired stack size
	 * 
	 * @author Mario Allemann
	 */
	public void setStackSize(String cardName, int size) {
		getCardStack(cardName).setSize(size);
	}

	/**
	 * Gets all the stacks from the battlefield. Used for initiating the battlefield
	 * on client side.
	 * 
	 * @return BFCardStack objects
	 * 
	 * @author Mario Allemann
	 */
	public ArrayList<BFCardStack> getAllStacks() {
		return allStacks;
	}
}
