package server.modelClasses;

import java.util.ArrayList;
import java.util.Collections;

import javafx.concurrent.Task;
import server.database.DataGrabber;
import server.resourceClasses.Battlefield;
import server.resourceClasses.Phase;
import server.resourceClasses.Player;
import server.resourceClasses.ServerConnector;
import shared.cardClasses.Card;
import shared.resourceClasses.GameMessage;
import shared.resourceClasses.MessageType;

/**
 * Handles game logic and communication with client
 * @author Mario Allemann
 * @author Philip Käppeli
 *
 */
public class ServerGameModel {
	protected ArrayList<Player> players;
	protected ArrayList<String> selectedActionCardNames;
	protected int startNumPlayers;
	protected Battlefield battlefield;
	protected int numOfEmptyStacks;
	protected boolean endConditionReached;
	protected ServerConnector connector;

	/**
	 * @param connector
	 *            The server connector, which all communication goes through
	 * @param numPlayers
	 *            The number of players, which want to play the game
	 * @param startNumPlayers
	 *            The number of players from the lobby
	 * @param selectedActionCardNames
	 *            The action cards which got selected by the lobby leader beforehand
	 * 
	 * @author Mario Allemann
	 */
	public ServerGameModel(ServerConnector connector, int startNumPlayers, ArrayList<String> selectedActionCardNames) {
		players = new ArrayList<Player>();
		this.connector = connector;
		this.startNumPlayers = startNumPlayers;
		this.selectedActionCardNames = selectedActionCardNames;
		battlefield = new Battlefield(selectedActionCardNames);
		numOfEmptyStacks = 0;
		endConditionReached = false;
	}

	/**
	 * Adds a player to the game
	 * 
	 * @param p
	 *            The player to be added
	 * @author Mario Allemann
	 */
	public void addPlayer(Player p) {
		players.add(p);
		if (players.size() == startNumPlayers) {
			prepareGame();
		}
	}

	/**
	 * Prepares the game start
	 * 
	 * @author Mario Allemann
	 */
	public void prepareGame() {
		int startCopperCards = 7;
		int startEstateCards = 3;
		int startHandSize = 5;
		// Randomize the play order
		Collections.shuffle(players);

		// Give every player the standard start cards
		for (Player p : players) {
			// Add 7 copper cards to each players draw stack
			for (int i = 0; i < startCopperCards; i++) {
				p.addCard(battlefield.drawCardFromStack("copper"));
			}
			// Add 3 estate cards to each players draw stack
			for (int i = 0; i < startEstateCards; i++) {
				p.addCard(battlefield.drawCardFromStack("estate"));
			}

			// Randomize the draw stack
			p.shuffleDrawStack();

			p.drawCards(startHandSize);
		}

		int twoPlayerGame = 8;
		int morePlayerGame = 12;

		// The stack size of the point cards on the battlefield depends on the number of
		// players
		if (startNumPlayers >= 3) {
			battlefield.setStackSize("estate", morePlayerGame);
			battlefield.setStackSize("duchy", morePlayerGame);
			battlefield.setStackSize("province", morePlayerGame);
		}

		if (startNumPlayers <= 2) {

			battlefield.setStackSize("estate", twoPlayerGame);
			battlefield.setStackSize("duchy", twoPlayerGame);
			battlefield.setStackSize("province", twoPlayerGame);

		}
		// Wake up the first player from the waiting phase
		players.get(0).nextPhase();

		// Gets the player names in order to be sent to the clients
		String[] playerNames = new String[players.size()];
		for (int i = 0; i < players.size(); i++) {
			playerNames[i] = players.get(i).getName();
		}

		// Sends all clients their cards and the battlefield
		for (Player p : players) {
			connector.send(new GameMessage(MessageType.PLAYERLIST, playerNames), p.getSocket());
			connector.send(new GameMessage(MessageType.PLAYERINDEX, "" + players.indexOf(p)), p.getSocket());
			connector.send(new GameMessage(MessageType.UPDATEDRAW, p.getDrawSize()), p.getSocket());
			connector.send(new GameMessage(MessageType.UPDATEHAND, p.getHandNames()), p.getSocket());
			connector.send(new GameMessage(MessageType.INITBFSTACKS, battlefield.getAllStacks()), p.getSocket());
			connector.send(new GameMessage(MessageType.UPDATEPHASE, p.getCurrentPhase().toString()), p.getSocket());

			// Start receiving messages from the clients
			createReceiveTask(p);
		}
	}

	/**
	 * JavaFX task used to receive messages from the clients. Manages chat messages,
	 * players trying to buy/play cards or change phase as well as clients leaving
	 * the game.
	 * 
	 * @param p
	 * @author Philip Käppeli
	 */
	public void createReceiveTask(Player p) {
		Task<Void> receiveTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				GameMessage msg;
				do {
					msg = connector.receive(p.getSocket());
					switch (msg.getType()) {
					case BUYCARD:
						buyCard(p, msg.getParameters()[0]);
						break;
					case PLAYCARD:
						int cardIndex = Integer.parseInt(msg.getParameters()[0]);
						String cardName = p.getHandNames()[cardIndex];
						boolean isPlayed = p.playCard(Integer.parseInt(msg.getParameters()[0]));
						if (isPlayed) {
							broadcast(new GameMessage(MessageType.OTHERPLAYED, "" + players.indexOf(p), cardName));
							connector.send(new GameMessage(MessageType.UPDATEDRAW, p.getDrawSize()), p.getSocket());
							connector.send(new GameMessage(MessageType.UPDATEHAND, p.getHandNames()), p.getSocket());
							connector.send(new GameMessage(MessageType.UPDATEPLAYED, p.getPlayedNames()),
									p.getSocket());
							// Special action cards, which are not implemented yet, could change the discard
							// stack
							connector.send(
									new GameMessage(MessageType.UPDATEDISCARD, p.getTopCardName(), p.getDiscardSize()),
									p.getSocket());
							broadcast(new GameMessage(MessageType.OTHERDISCARD, "" + players.indexOf(p),
									p.getTopCardName()));
						} else {
							connector.send(new GameMessage(MessageType.DENYPLAY), p.getSocket());
						}
						break;

					case NEXTPHASE:
						if (p.getCurrentPhase() == Phase.BUY) {

							if (endConditionReached) {
								// The last cleanup has to be executed in order to calcualte the correct number
								// of turns
								p.cleanUp();
								// Game end reached
								broadcast(new GameMessage(MessageType.ENDGAME, getScoreList()));
							} else {
								// let switch players
								p.nextPhase();
								p.cleanUp();
								connector.send(new GameMessage(MessageType.UPDATEDRAW, p.getDrawSize()), p.getSocket());
								connector.send(new GameMessage(MessageType.UPDATEHAND, p.getHandNames()),
										p.getSocket());
								connector.send(new GameMessage(MessageType.UPDATEPLAYED, p.getPlayedNames()),
										p.getSocket());
								connector.send(new GameMessage(MessageType.UPDATEDISCARD, p.getTopCardName(),
										p.getDiscardSize()), p.getSocket());
								broadcast(new GameMessage(MessageType.OTHERDISCARD, "" + players.indexOf(p),
										p.getTopCardName()));
								p.nextPhase();
								changePlayer(p);
							}
						} else {
							p.nextPhase();
						}
						connector.send(new GameMessage(MessageType.UPDATEPHASE, p.getCurrentPhase().toString()),
								p.getSocket());
						break;
					case CHATMESSAGE:
						broadcast(msg);
						break;
					case ENDOFCONNECTION:
						connector.send(msg, p.getSocket());
						if (!endConditionReached) {
							// Game is still going on -> remove player
							removePlayer(p);
						}
						p.getSocket().close();
						// Game will no longer be running after this
						players.remove(p);
						if (players.size() == 0) {
							connector.setGameRunning(false);
						}
						break;
					default:
						// do nothing
						break;
					}
				} while (msg.getType() != MessageType.ENDOFCONNECTION);
				return null;
			}
		};
		new Thread(receiveTask).start();
	}

	/**
	 * Changes player to the next one in the list
	 * 
	 * @param p
	 *            The current player
	 * @author Mario Allemann
	 */
	public void changePlayer(Player p) {
		int currPlayerIndex = players.indexOf(p);

		// Change index to next player
		if (currPlayerIndex < players.size() - 1) {
			currPlayerIndex++;
		}
		// If it's the last player, loop back around and start with the first player
		else {
			currPlayerIndex = 0;
		}

		Player pNext = players.get(currPlayerIndex);
		// Wake up the next player from waiting phase
		pNext.nextPhase();
		// Tell the client to unlock its GUI
		connector.send(new GameMessage(MessageType.UPDATEPHASE, pNext.getCurrentPhase().toString()), pNext.getSocket());
	}

	/**
	 * Buys a card from the the battlefield
	 * 
	 * @param player
	 *            The player object which wants to buy a card
	 * @param cardName
	 *            The desired card named
	 * @author Mario Allemann
	 */
	public void buyCard(Player player, String cardName) {
		// Look at the card the player wants to buy without removing it
		Card toBuy = battlefield.peekCard(cardName);
		// If the player has no buys left or has not enough money send message to
		// client, then immediately return
		if (player.getBuysLeft() <= 0 || player.getMoneyLeft() < toBuy.getCost()) {
			// Buy unsuccessful: send message to client
			connector.send(new GameMessage(MessageType.DENYBUY), player.getSocket());
			return;
		}

		battlefield.drawCardFromStack(cardName);
		player.buyCard(toBuy);
		// Buy successful: send the updated battlefieldStack & discardStack size to all
		// clients
		int newStackSize = battlefield.getCardStack(cardName).getSize();
		broadcast(new GameMessage(MessageType.UPDATEBFSTACKS, cardName, "" + newStackSize));

		connector.send(new GameMessage(MessageType.UPDATEDISCARD, cardName, player.getDiscardSize()),
				player.getSocket());
		// Updates the top card of the discard stack to be seen for the other players
		broadcast(new GameMessage(MessageType.OTHERDISCARD, "" + players.indexOf(player), cardName));

		// If the stack got emptied count up the empty battlefield decks and check end
		// condition
		if (newStackSize <= 0) {
			numOfEmptyStacks++;
			// The only trigger to end an game is after a card is bought
			checkGameEnd();
		}

	}

	/**
	 * Sends a game message to all clients that are in this game
	 * 
	 * @author Philip Käppeli
	 * @param msg
	 */
	public void broadcast(GameMessage msg) {
		for (Player p : players) {
			connector.send(msg, p.getSocket());
		}
	}

	/**
	 * Checks whether the end condition got reached
	 * 
	 * @author Mario Allemann
	 */
	public void checkGameEnd() {
		// If 3 stacks are empty OR the Province stack is empty, the game end is reached
		if (numOfEmptyStacks >= 3 || (battlefield.getCardStack("province").getSize() <= 0)) {
			endConditionReached = true;
		}
	}

	/**
	 * Removes a player from the game
	 * 
	 * @param playerToRemove
	 *            The player to be removed
	 * 
	 * @author Mario Allemann
	 */
	void removePlayer(Player playerToRemove) {
		// If less than 2 players will be left after removing, cancel the game
		if (players.size() <= 2) {
			players.remove(playerToRemove);
			// Cancel game for remaining players
			for (Player p : players) {
				connector.send(new GameMessage(MessageType.CANCELGAME), p.getSocket());
			}
		} else {
			int index = players.indexOf(playerToRemove);
			// If it's the players turn, switch player
			if (playerToRemove.getCurrentPhase() != Phase.WAITING) {
				changePlayer(playerToRemove);
			}
			// Remove player
			players.remove(playerToRemove);
			// tell the client so he can update the "otherPlayed" fields
			broadcast(new GameMessage(MessageType.PLAYERLEFT, "" + index));
		}
	}

	/**
	 * Calculates the players scores and adds them to the database
	 * 
	 * @return An array with the player scores (name;score)
	 * @author Mario Allemann
	 */
	public String[] getScoreList() {
		ArrayList<String> playerScores = new ArrayList<String>();

		for (Player p : players) {
			p.calculateScore();
		}

		Collections.sort(players);
		DataGrabber dg = new DataGrabber();
		// Add name;score to list and to database
		for (Player p : players) {
			playerScores.add(p.getName() + ";" + p.getScore());
			dg.addHighscore(p.getName(), p.getScore());
		}
		return playerScores.toArray(new String[0]);
	}
}
