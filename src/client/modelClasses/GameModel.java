package client.modelClasses;

import client.ServiceLocator;
import client.commonClasses.Translator;
import client.resourceClasses.ClientConnector;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.BFCardStack;
import shared.abstractClasses.Model;
import shared.resourceClasses.MessageType;

/**
 * Model class for the game mvc
 * 
 * @author Philip Käppeli
 *
 */
public class GameModel extends Model {
	protected final int MAX_PAGESIZE = 5;
	protected final int MAX_PLAYERS = 4;
	// CONNECTION
	protected int port;
	protected String hostname;
	protected String username;
	protected ClientConnector client;
	// PLAYER
	protected int playerIndex;
	protected StringProperty drawSize;
	protected ObservableList<String> playerHand = FXCollections.observableArrayList();
	protected ObservableList<String> playerPlayed = FXCollections.observableArrayList();
	protected ObservableList<String> discardStack = FXCollections.observableArrayList();
	protected StringProperty currentPhase;
	protected StringProperty denyMessage;
	// BATTLEFIELD
	protected ObservableList<BFCardStack> BFStacks = FXCollections.observableArrayList();
	protected ObservableList<String> BFchange = FXCollections.observableArrayList();
	protected ObservableList<String> selActionCards = FXCollections.observableArrayList();
	protected ObservableList<String> moneyCards = FXCollections.observableArrayList();
	protected ObservableList<String> pointCards = FXCollections.observableArrayList();
	// OTHER PLAYERS
	protected ObservableList<String> otherPlayers = FXCollections.observableArrayList();
	protected ObservableList<String> otherPlayed = FXCollections.observableArrayList();
	protected ObservableList<String> otherDiscard = FXCollections.observableArrayList();
	// GAME END
	protected String[] scoreList;
	// local stuff
	protected String[] playerList;
	protected int currentHandPage;
	protected int numHandPages;
	protected int currentPlayedPage;
	protected int numPlayedPages;
	protected boolean buttonsTempLocked;
	protected IntegerProperty endCountDown;
	// Pascal: Chat
	protected ObservableList<String> chatMessages = FXCollections.observableArrayList();

	/**
	 * CONSTRUCTOR: sets default variable values
	 * 
	 * @author Philip Käppeli
	 */
	public GameModel() {
		// Init
		setButtonsTempLocked(false);
		setCurrentHandPage(0);
		setCurrentPlayedPage(0);
		setDrawSize(new SimpleStringProperty());
		setCurrentPhase(new SimpleStringProperty());
		setDenyMessage(new SimpleStringProperty());
		setEndCountDown(new SimpleIntegerProperty());
		getCurrentPhase().set("waiting");
		getEndCountDown().set(15);

	}

	/**
	 * Build the other players list based on the players list and the player index
	 * 
	 * @author Philip Käppeli
	 */
	public void parsePlayerList(String playerIndexStr) {
		playerIndex = Integer.parseInt(playerIndexStr);
		getOtherPlayers().clear();
		getOtherPlayed().clear();
		getOtherDiscard().clear();
		for (int i = 0; i < this.playerList.length; i++) {
			if (i != getPlayerIndex()) {
				getOtherPlayers().add(this.playerList[i]);
			}
			getOtherPlayed().add("empty");
			getOtherDiscard().add("empty");
		}
	}

	/**
	 * Remove a player from the player list and re-build the other player list
	 * 
	 * @author Philip Käppeli
	 */
	public void removePlayer(String indexStr) {
		int remIndex = Integer.parseInt(indexStr);
		String[] playerListNew = new String[playerList.length - 1];
		int count = 0;
		// Build the new player list
		for (int i = 0; i < playerList.length; i++) {
			if (i != remIndex) {
				playerListNew[count] = playerList[count];
				count++;
			}
		}
		playerList = null;
		playerList = playerListNew;
		// Change own index if necessary
		if (remIndex < getPlayerIndex()) {
			playerIndex = getPlayerIndex() - 1;
		}
		// Re-parse the list
		parsePlayerList("" + this.getPlayerIndex());
	}

	/**
	 * The player wants to buy a certain card from the battlefield tell the server
	 * to handle this
	 * 
	 * @author Philip Käppeli
	 * @param name
	 */
	public void buyCard(String name) {
		if (!isButtonsTempLocked()) {
			getClient().sendMessage(MessageType.BUYCARD, name);
			setButtonsTempLocked(true);
		}
	}

	/**
	 * The player wants to play a certain card from his hand tell the server to
	 * handle this
	 * 
	 * @author Philip Käppeli
	 * @param cardIndex
	 */
	public void playCard(int cardIndex) {
		// Send message to server
		if (!isButtonsTempLocked()) {
			getClient().sendMessage(MessageType.PLAYCARD, "" + cardIndex);
			setButtonsTempLocked(true);
		}
	}

	/**
	 * The player wants to enter the next phase tell the server to handle this
	 * 
	 * @author Philip Käppeli
	 */
	public void nextPhase() {
		getClient().sendMessage(MessageType.NEXTPHASE);
	}

	/**
	 * Set size of a certain stack
	 * 
	 * @author Philip Käppeli
	 * @param name
	 * @param size
	 */
	public void setBFStackSize(String name, String size) {
		for (BFCardStack bfc : getBFStacks()) {
			if (bfc.getName().equals(name)) {
				bfc.setSize(Integer.parseInt(size));
			}
		}
	}

	/**
	 * Return size of a certain stack
	 * 
	 * @author Philip Käppeli
	 * @param name
	 * @return size of the stack
	 */
	public int getBFStackSize(String name) {
		for (BFCardStack bfc : getBFStacks()) {
			if (bfc.getName().equals(name)) {
				return bfc.getSize();
			}
		}
		return 0;
	}

	/**
	 * Game has ended. Set the according phase and start countdown
	 * 
	 * @author Philip Käppeli
	 */
	public void endGame(String[] scoreList) {
		this.scoreList = scoreList;
		getCurrentPhase().set("end");
		// Run countdown before returning to the lobby
		while (getEndCountDown().get() > 0) {
			Platform.runLater(() -> {
				getEndCountDown().set(getEndCountDown().get() - 1);
			});
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * The game has been cancelled. Set the according phase
	 * 
	 * @author Philip Käppeli
	 */
	public void cancelGame() {
		getCurrentPhase().set("cancel");
	}

	/**
	 * Disconnect client from the server
	 * 
	 * @author Philip Käppeli
	 */
	public void disconnectPlayer() {
		getClient().disconnect();
	}

	/**
	 * GETTERS and SETTERS
	 * 
	 * @author Philip Käppeli
	 */
	public String getCurrentPhaseStr() {
		Translator t = ServiceLocator.getServiceLocator().getTranslator();
		String phaseString = t.getString("game.currentPhase");
		switch (getCurrentPhase().get()) {
		case "action":
			phaseString += t.getString("game.phaseAction");
			break;

		case "buy":
			phaseString += t.getString("game.phaseBuy");
			break;

		case "waiting":
			phaseString += t.getString("game.phaseWaiting");
			break;
		}
		return phaseString;
	}

	public String[] getScoreList() {
		return this.scoreList;
	}

	public void setPlayerList(String[] playerList) {
		this.playerList = playerList;
	}

	public int getMAX_PAGESIZE() {
		return MAX_PAGESIZE;
	}

	public int getMAX_PLAYERS() {
		return MAX_PLAYERS;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ClientConnector getClient() {
		return client;
	}

	public void setClient(ClientConnector client) {
		this.client = client;
	}

	public int getPlayerIndex() {
		return playerIndex;
	}

	public StringProperty getDrawSize() {
		return drawSize;
	}

	public void setDrawSize(StringProperty drawSize) {
		this.drawSize = drawSize;
	}

	public ObservableList<String> getPlayerHand() {
		return playerHand;
	}

	public void setPlayerHand(ObservableList<String> playerHand) {
		this.playerHand = playerHand;
	}

	public ObservableList<String> getPlayerPlayed() {
		return playerPlayed;
	}

	public void setPlayerPlayed(ObservableList<String> playerPlayed) {
		this.playerPlayed = playerPlayed;
	}

	public ObservableList<String> getDiscardStack() {
		return discardStack;
	}

	public void setDiscardStack(ObservableList<String> discardStack) {
		this.discardStack = discardStack;
	}

	public StringProperty getCurrentPhase() {
		return currentPhase;
	}

	public void setCurrentPhase(StringProperty currentPhase) {
		this.currentPhase = currentPhase;
	}

	public StringProperty getDenyMessage() {
		return denyMessage;
	}

	public void setDenyMessage(StringProperty denyMessage) {
		this.denyMessage = denyMessage;
	}

	public ObservableList<String> getOtherPlayed() {
		return otherPlayed;
	}

	public void setOtherPlayed(ObservableList<String> otherPlayed) {
		this.otherPlayed = otherPlayed;
	}

	public ObservableList<String> getOtherDiscard() {
		return otherDiscard;
	}

	public void setOtherDiscard(ObservableList<String> otherDiscard) {
		this.otherDiscard = otherDiscard;
	}

	public ObservableList<BFCardStack> getBFStacks() {
		return BFStacks;
	}

	public void setBFStacks(ObservableList<BFCardStack> bFStacks) {
		BFStacks = bFStacks;
	}

	public ObservableList<String> getMoneyCards() {
		return moneyCards;
	}

	public void setMoneyCards(ObservableList<String> moneyCards) {
		this.moneyCards = moneyCards;
	}

	public ObservableList<String> getPointCards() {
		return pointCards;
	}

	public void setPointCards(ObservableList<String> pointCards) {
		this.pointCards = pointCards;
	}

	public ObservableList<String> getSelActionCards() {
		return selActionCards;
	}

	public void setSelActionCards(ObservableList<String> selActionCards) {
		this.selActionCards = selActionCards;
	}

	public ObservableList<String> getBFchange() {
		return BFchange;
	}

	public void setBFchange(ObservableList<String> bFchange) {
		BFchange = bFchange;
	}

	public int getCurrentHandPage() {
		return currentHandPage;
	}

	public void setCurrentHandPage(int currentHandPage) {
		this.currentHandPage = currentHandPage;
	}

	public int getCurrentPlayedPage() {
		return currentPlayedPage;
	}

	public void setCurrentPlayedPage(int currentPlayedPage) {
		this.currentPlayedPage = currentPlayedPage;
	}

	public int getNumHandPages() {
		return numHandPages;
	}

	public void setNumHandPages(int numHandPages) {
		this.numHandPages = numHandPages;
	}

	public int getNumPlayedPages() {
		return numPlayedPages;
	}

	public void setNumPlayedPages(int numPlayedPages) {
		this.numPlayedPages = numPlayedPages;
	}

	public ObservableList<String> getOtherPlayers() {
		return otherPlayers;
	}

	public void setOtherPlayers(ObservableList<String> otherPlayers) {
		this.otherPlayers = otherPlayers;
	}

	public IntegerProperty getEndCountDown() {
		return endCountDown;
	}

	public void setEndCountDown(IntegerProperty endCountDown) {
		this.endCountDown = endCountDown;
	}

	public ObservableList<String> getChatMessages() {
		return chatMessages;
	}

	public void setChatMessages(ObservableList<String> chatMessages) {
		this.chatMessages = chatMessages;
	}

	public boolean isButtonsTempLocked() {
		return buttonsTempLocked;
	}

	public void setButtonsTempLocked(boolean buttonsTempLocked) {
		this.buttonsTempLocked = buttonsTempLocked;
	}
}