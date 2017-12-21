package server.resourceClasses;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

import shared.CardStack;
import shared.cardClasses.*;
import shared.resourceClasses.StackType;

/**
 * 
 * @author Mario Allemann
 *
 */
public class Player implements Comparable<Player> {
	protected String name;
	protected int playerID;
	protected CardStack drawStack;
	protected ArrayList<Card> played;
	protected CardStack discardStack;
	protected CardStack trashStack;
	protected ArrayList<Card> hand;
	protected Phase currentPhase;
	protected int moneyLeft;
	protected int actionsLeft;
	protected int buysLeft;
	protected int numOfTurns;
	protected int score;
	protected Socket socket;

	/**
	 * 
	 * @param name
	 *            The players name
	 * @param socket
	 *            The socket, which is used for client communication
	 * @author Mario Allemann
	 * 
	 */
	public Player(String name, Socket socket) {
		super();
		this.socket = socket;
		this.name = name;
		drawStack = new CardStack(StackType.DRAW);
		played = new ArrayList<Card>();
		discardStack = new CardStack(StackType.DISCARD);
		trashStack = new CardStack(StackType.TRASH);
		hand = new ArrayList<Card>();
		currentPhase = Phase.WAITING;
		moneyLeft = 0;
		actionsLeft = 1;
		buysLeft = 1;
		score = 0;
		numOfTurns = 0;
	}

	/**
	 * Adds a card to the draw stack
	 * 
	 * @param c
	 *            The card object to be added
	 * @author Mario Allemann
	 */
	public void addCard(Card c) {
		drawStack.addCard(c);
	}

	/**
	 * Plays a card from the players hand
	 * 
	 * @param handIndex
	 *            The position of the card in the hand
	 * @return true if the card could be played, false if it couldn't
	 * @author Mario Allemann
	 */
	public boolean playCard(int handIndex) {
		Card card = hand.get(handIndex);
		boolean isPlayed = false;

		// Determine which kind of card got selected, create an instance of it, and
		// activate its effects. Point cards get blocked from playing by the client GUI
		if (card instanceof ActionCard) {
			ActionCard ac = (ActionCard) card;
			isPlayed = playActionCard(ac);

		}

		if (card instanceof MoneyCard) {
			MoneyCard mc = (MoneyCard) card;
			moneyLeft += mc.getValue();
			isPlayed = true;
		}

		// Only move the card to the discard stack if it could be played
		if (isPlayed) {
			moveToPlayed(handIndex);
		}

		return isPlayed;

	}

	/**
	 * Gets the card name on top of the discard stack. This information has to be
	 * visible for all players.
	 * 
	 * @return The card name at the top of the discard stack
	 * @author Mario Allemann
	 */
	public String getTopCardName() {
		// If the discard is empty, return empty (client will show a placeholder image)
		if (discardStack.getStack().isEmpty()) {
			return "empty";
		}
		return discardStack.getStack().peek().getName();
	}

	/**
	 * Gets the size of the discard stack. Used by the client
	 * 
	 * @return The size of the discard stack
	 * @author Mario Allemann
	 * 
	 **/
	public String getDiscardSize() {
		return Integer.toString(discardStack.getStack().size());

	}

	/**
	 * Gets the size of the draw stack. Used by the client
	 * 
	 * @return The size of the draw stack
	 * @author Mario Allemann
	 */
	public String getDrawSize() {
		return Integer.toString(drawStack.getStack().size());
	}

	/**
	 * Plays a action card from hand and activates its effects
	 * 
	 * @param ac
	 *            The ActionCard to be played
	 * @return true, if the card could be played, false if there are no action left
	 * 
	 * @author Mario Allemann
	 */
	private boolean playActionCard(ActionCard ac) {
		// Checks whether the player has actions left
		if (actionsLeft <= 0) {
			return false;
		}
		// Activate the cards special action first
		if (ac.getSpecialActionID() != 0) {
			// Activate the special action in the SpecialActionCard class
		}

		// Execute the simple cards effects
		actionsLeft += ac.getPlusAction();
		drawCards(ac.getPlusDraw());
		moneyLeft += ac.getPlusMoney();
		buysLeft += ac.getPlusBuy();

		// The cards effect were activated, thus the card was successfully played
		actionsLeft--;
		return true;

	}

	/**
	 * Moves a played card to the played stack and removes it from hand
	 * 
	 * @param handIndex
	 *            The position of the card in the hand
	 * 
	 * @author Mario Allemann
	 */
	public void moveToPlayed(int handIndex) {
		played.add(hand.get(handIndex));
		hand.remove(handIndex);
	}

	/**
	 * Draws cards from the draw stack
	 * 
	 * @param plusDraw
	 *            The number of cards to draw
	 * 
	 * @author Mario Allemann
	 */
	public void drawCards(int plusDraw) {
		// Immediately return if the action card has no draw effect
		if (plusDraw <= 0) {
			return;
		}

		int drawnCards = 0;
		boolean alreadyShuffled = false;
		while (drawnCards < plusDraw) {
			// If the draw stack is empty, shuffle the discard stack and provide it as the
			// new draw stack
			if (drawStack.size() <= 0) {
				// The stack can only be shuffled once per draw action. If the player has all
				// cards in his hand, no more cards can be drawn as all his stacks are empty.
				if (alreadyShuffled || (discardStack.size() <= 0)) {
					return;
				}
				shuffle();
				alreadyShuffled = true;
			}
			hand.add(drawStack.removeCard());
			drawnCards++;
		}

	}

	/**
	 * Adds a card to the discard stack
	 * 
	 * @param c
	 *            The card to be discarded
	 * 
	 * @author Mario Allemann
	 */
	public void discardCard(Card c) {
		discardStack.addCard(c);

	}

	/**
	 * Shuffles the discard stack and provides it as new draw stack
	 * 
	 * @author Mario Allemann
	 */
	public void shuffle() {
		Collections.shuffle(discardStack.getStack());
		drawStack = discardStack;
		discardStack = new CardStack(StackType.DISCARD);
	}

	/**
	 * Trashes a card from the players hand
	 * 
	 * @param handIndex
	 *            The position of the card in the hand
	 * @return The card which got trashed
	 * 
	 * @author Mario Allemann
	 */
	public Card trashCard(int handIndex) {
		Card c = hand.get(handIndex);
		hand.remove(handIndex);
		return c;
	}

	/**
	 * Puts the player into the next game phase
	 * 
	 * @author Mario Allemann
	 */
	public void nextPhase() {
		currentPhase = currentPhase.getNextPhase(currentPhase);
	}

	/**
	 * Buys a card from the battlefield and adds its to the discard stack
	 * 
	 * @param card
	 *            The card to buy
	 * 
	 * @author Mario Allemann
	 */
	public void buyCard(Card card) {
		discardStack.addCard(card);
		moneyLeft -= card.getCost();
		buysLeft--;

	}

	/**
	 * At the end of the players turn, prepare him for the next turn
	 * 
	 * @author Mario Allemann
	 */
	public void cleanUp() {

		// Iterates through played stack and puts them to discard stack
		for (Card c : played) {
			discardStack.addCard(c);
		}
		played.clear();

		// Iterates through the remaining hand cards and puts them to the discard stack
		for (Card c : hand) {
			discardStack.addCard(c);
		}
		hand.clear();

		resetLeftVariables();

		drawCards(5);
		numOfTurns++;

	}

	/**
	 * At the start of each turn the player has one action and one buy by default
	 * 
	 * @author Mario Allemann
	 */
	public void resetLeftVariables() {
		actionsLeft = 1;
		moneyLeft = 0;
		buysLeft = 1;
	}

	/**
	 * Calculates the score determined by the point cards
	 * 
	 * @author Mario Allemann
	 */
	public void calculateScore() {
		moveAllToDiscard();
		for (Card c : discardStack.getStack()) {
			if (c instanceof PointCard) {
				PointCard pc = (PointCard) c;
				score += pc.getValue();
			}
		}
	}

	/**
	 * Adds all cards to the discard stack in order to calculate the score more
	 * easily
	 * 
	 * @author Mario Allemann
	 */
	private void moveAllToDiscard() {
		for (Card c : drawStack.getStack()) {
			discardStack.addCard(c);

		}
		for (Card c : hand) {
			discardStack.addCard(c);
		}

		for (Card c : played) {
			discardStack.addCard(c);
		}
	}

	/**
	 * Shuffles the draw stack
	 * 
	 * @author Mario Allemann
	 */
	public void shuffleDrawStack() {
		Collections.shuffle(drawStack.getStack());
	}

	/**
	 * Provides the hand as Strings for the client
	 * 
	 * @return A String array with the card names
	 * 
	 * @author Mario Allemann
	 */
	public String[] getHandNames() {
		ArrayList<String> handNames = new ArrayList<String>();
		for (Card c : hand) {
			handNames.add(c.getName());
		}
		return handNames.toArray(new String[0]);
	}

	/**
	 * Provides the played cards as Strings for the client
	 * 
	 * @return A String array with the card names
	 * 
	 * @author Mario Allemann
	 */
	public String[] getPlayedNames() {
		ArrayList<String> playedNames = new ArrayList<String>();

		for (Card c : played) {
			playedNames.add(c.getName());
		}
		return playedNames.toArray(new String[0]);
	}

	/**
	 * The last played card needs to be displayed to the other players
	 * 
	 * @return The last played card
	 * 
	 * @author Mario Allemann
	 */
	public String getLastPlayed() {
		return played.get(played.size() - 1).getName();
	}

	/**
	 * Compares the players by their score and number of turns
	 * 
	 * @author Mario Allemann
	 */
	@Override
	public int compareTo(Player o) {
		// The player with the fewer turns wins, if the score is the same
		if (o.getScore() - score == 0) {
			return numOfTurns - o.getNumOfTurns();
		}
		return o.getScore() - score;
	}

	// *********************************************
	// GETTERS & SETTERS, DEBUG METHODS
	// *********************************************

	public int getBuysLeft() {
		return buysLeft;
	}

	public int getMoneyLeft() {
		return moneyLeft;
	}

	public String getName() {
		return name;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getScore() {
		return score;
	}

	public int getNumOfTurns() {
		return numOfTurns;
	}

	public Phase getCurrentPhase() {
		return currentPhase;
	}
}
