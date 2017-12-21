package client.resourceClasses;

import java.net.Socket;

import client.ServiceLocator;
import client.commonClasses.Translator;
import client.modelClasses.GameModel;
import client.modelClasses.MenuModel;
import javafx.concurrent.Task;
import shared.abstractClasses.Model;
import shared.resourceClasses.GameMessage;
import shared.resourceClasses.MessageType;

/**
 * This class is the central point used for communication between the client and
 * a server. It manages setting up the connection as well as sending/receiving
 * messages.
 * 
 * @author Philip Käppeli
 *
 */
public class ClientConnector {
	protected int port;
	protected String hostName;
	protected MessageType connectionType;
	protected Socket socket;
	protected MenuModel mModel;
	protected GameModel gModel;
	protected String userName;
	protected boolean requestedClose;

	/**
	 * Sets up a clientTask connection according to the connection type and
	 * currently used model
	 * 
	 * @author Philip Käppeli
	 * @param connectionType
	 * @param model
	 */
	public void createConnection(MessageType connectionType, Model model) {
		this.connectionType = connectionType;
		// Currently used model is a menu model
		if (model instanceof MenuModel) {
			this.mModel = (MenuModel) model;
			this.userName = mModel.getUsername();
			this.port = mModel.getPort();
			this.hostName = mModel.getHostname();
		}

		// Currently used model is a game model
		if (model instanceof GameModel) {
			this.gModel = (GameModel) model;
			this.userName = gModel.getUsername();
			this.port = gModel.getPort();
			this.hostName = gModel.getHostname();
		}
		new Thread(clientTask).start();
	}

	/**
	 * JavaFX Task used to communicate with the server without blocking any client
	 * gui's.
	 * 
	 * @author Philip Käppeli
	 */
	final Task<Void> clientTask = new Task<Void>() {
		@Override
		protected Void call() throws Exception {
			try {
				// Connect to the server
				socket = new Socket(hostName, port);
				// Connection successful
				if (mModel != null) {
					mModel.connectionAccepted.set(1);
				}
				requestedClose = false;

				if (connectionType != MessageType.HANDSHAKE) {
					// Send connection type to the server
					new GameMessage(connectionType, userName).send(getSocket());
					GameMessage msg;

					// Receive message until the connection gets closed
					do {
						msg = GameMessage.receive(getSocket());
						Translator t = ServiceLocator.getServiceLocator().getTranslator();
						// Handle different menu message types
						switch (msg.getType()) {
						// SPLASH
						case VERSIONOK:
							requestedClose = true;
							mModel.isVersionOk.set(true);
							mModel.updateVersionNum(msg.getParameters()[0]);
							break;
						case UPDATEFILES:
							mModel.updateFiles(msg.getFiles(), msg.getLocaleStr());
							break;
						// LOBBY
						case UPDATELOBBY:
							mModel.updateLobbyList(msg.getParameters());
							break;
						case SETLOBBYLEADER:
							mModel.setLobbyLeader();
							mModel.setAvailActionCards(msg.getParameters());
							break;
						case LOBBYREADY:
							// Tell the lobbyleader that the lobby is ready to go
							if (mModel.isLobbyLeader.get()) {
								mModel.isLobbyReady.set(true);
							}
							break;
						case LOBBYFULL:
							// lobby is full already - return to the main menu
							mModel.lobbyFullMsg.set(t.getString("lobby.lobbyFull"));
							break;
						case CHATMESSAGE:
							if (mModel != null) {
								mModel.chatMessages.add(msg.getParameters()[0]);
							}
							if (gModel != null) {
								gModel.getChatMessages().add(msg.getParameters()[0]);
							}
							break;
						case STARTGAME:
							requestedClose = true;
							mModel.startGame.set(true);
							break;
						case GAMERUNNING:
							// couldn't start the game because another game is currently
							// running
							mModel.gameRunningMsg.set(t.getString("lobby.gameRunningMsg"));
							break;

						// HIGHSCORES
						case GETHIGHSCORES:
							requestedClose = true;
							mModel.updateHighscoreList(msg.getParameters());
							break;

						// GAME
						case PLAYERLIST:
							gModel.setPlayerList(msg.getParameters());
							break;
						case PLAYERINDEX:
							gModel.parsePlayerList(msg.getParameters()[0]);
							break;
						case INITBFSTACKS:
							gModel.getBFStacks().addAll(msg.getStackList());
							break;
						case OTHERPLAYED:
							int index1 = Integer.parseInt(msg.getParameters()[0]);
							String cardName1 = msg.getParameters()[1];
							gModel.getOtherPlayed().set(index1, "empty");
							gModel.getOtherPlayed().set(index1, cardName1);
							break;
						case OTHERDISCARD:
							int index2 = Integer.parseInt(msg.getParameters()[0]);
							String cardName2 = msg.getParameters()[1];
							gModel.getOtherDiscard().set(index2, "empty");
							gModel.getOtherDiscard().set(index2, cardName2);
							break;
						case PLAYERLEFT:
							gModel.removePlayer(msg.getParameters()[0]);
							break;
						case UPDATEBFSTACKS:
							gModel.getBFchange().clear();
							gModel.getBFchange().addAll(msg.getParameters()[0], msg.getParameters()[1]);
							break;
						case UPDATEDRAW:
							gModel.getDrawSize().set(msg.getParameters()[0]);
							break;
						case UPDATEHAND:
							gModel.getPlayerHand().clear();
							for (String s : msg.getParameters()) {
								gModel.getPlayerHand().add(s);
							}
							break;
						case UPDATEPLAYED:
							gModel.getPlayerPlayed().clear();
							for (String s : msg.getParameters()) {
								gModel.getPlayerPlayed().add(s);
							}
							break;
						case UPDATEPHASE:
							gModel.getCurrentPhase().set(msg.getParameters()[0].toLowerCase());
							break;
						case UPDATEDISCARD:
							gModel.getDiscardStack().clear();
							gModel.getDiscardStack().addAll(msg.getParameters()[0], msg.getParameters()[1]);
							gModel.setButtonsTempLocked(false);
							break;
						case DENYBUY:
							gModel.getDenyMessage().set(t.getString("game.denyBuy"));
							gModel.setButtonsTempLocked(false);
							break;
						case DENYPLAY:
							gModel.getDenyMessage().set(t.getString("game.denyPlay"));
							gModel.setButtonsTempLocked(false);
							break;
						case ENDGAME:
							requestedClose = true;
							gModel.endGame(msg.getParameters());
							break;
						case CANCELGAME:
							requestedClose = true;
							gModel.cancelGame();
							break;

						// GENERAL
						case ENDOFCONNECTION:
							// client initiated disconnect
							if (requestedClose) {
								getSocket().close();
							} else {
								// connection lost, do nothing
							}
							break;
						default:
							// do nothing
							break;
						}
					} while (msg.getType() != MessageType.ENDOFCONNECTION);
				}
			} catch (Exception e) {
				e.printStackTrace();
				// Connection failed
				if (mModel != null) {
					mModel.connectionAccepted.set(-1);
				}
			}
			return null;
		}
	};

	/**
	 * Receives a message from the server
	 * 
	 * @author Philip Käppeli
	 * @return the received GameMessage
	 */
	public GameMessage receiveMessage() {
		return GameMessage.receive(getSocket());
	}

	/**
	 * Sends a message to the server
	 * 
	 * @author Philip Käppeli
	 * @param type
	 * @param parameters
	 */
	public void sendMessage(MessageType type, String... parameters) {
		new GameMessage(type, parameters).send(getSocket());
	}

	/**
	 * Start the disconnect progress by telling the server that the client wants to
	 * stop the connection
	 * 
	 * @author Philip Käppeli
	 */
	public void disconnect() {
		requestedClose = true;
		try {
			// Tell the server to close the connection
			if (getSocket() != null && !getSocket().isClosed()) {
				sendMessage(MessageType.ENDOFCONNECTION);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// GETTER socket
	public Socket getSocket() {
		return socket;
	}
}
