package server.modelClasses;

import java.net.Socket;
import java.util.ArrayList;

import javafx.concurrent.Task;
import server.database.CardPair;
import server.database.DataGrabber;
import server.resourceClasses.ServerConnector;
import shared.resourceClasses.GameMessage;
import shared.resourceClasses.MessageType;

/**
 * This class manages the communication between the server and clients that try
 * to join or are currently in a lobby.
 * 
 * @author Philip Käppeli
 *
 */
public class ServerLobbyModel {
	protected ServerConnector connector;
	protected Socket client;
	protected String userName;
	protected static ArrayList<Socket> lobbySockets;
	protected static ArrayList<String> lobbyNames;

	/**
	 * 
	 * CONSTRUCTOR: Handles clients trying to join the lobby. Decides whether to let
	 * them or deny them if the lobby is full. It also informs the clients when the
	 * lobby is ready to start a game.
	 * 
	 * @author Philip Käppeli
	 * @param connector
	 * @param client
	 * @param type
	 * @param name
	 */
	public ServerLobbyModel(ServerConnector connector, Socket client, MessageType type, String name) {
		this.client = client;
		this.connector = connector;
		userName = name;

		// Initialize static arraylists if null
		if (ServerLobbyModel.lobbySockets == null) {
			ServerLobbyModel.lobbySockets = new ArrayList<Socket>();
		}
		if (ServerLobbyModel.lobbyNames == null) {
			ServerLobbyModel.lobbyNames = new ArrayList<String>();
		}

		// Check if there is still room in the lobby
		if (ServerLobbyModel.lobbySockets.isEmpty() || ServerLobbyModel.lobbySockets.size() < 4) {
			// There is still room -> join
			ServerLobbyModel.lobbyNames.add(userName);
			ServerLobbyModel.lobbySockets.add(client);
			broadcast(new GameMessage(MessageType.UPDATELOBBY, ServerLobbyModel.lobbyNames.toArray(new String[0])));

			// The lobby is now ready to start the game
			if (ServerLobbyModel.lobbySockets.size() >= 2) {
				broadcast(new GameMessage(MessageType.LOBBYREADY));
			}
		} else {
			// Lobby is full already
			connector.send(new GameMessage(MessageType.LOBBYFULL), client);
		}
		// Wait for messages from client
		waitForMessages();
	}

	/**
	 * Get the names of all available action cards from the data base and returns
	 * them as a string array. This is used to pass these names on to the lobby
	 * leader.
	 * 
	 * @author Philip Käppeli
	 * @return String array containing all action card names found in the data base.
	 */
	public String[] getActionCardNames() {
		DataGrabber dg = new DataGrabber();
		ArrayList<CardPair> actionCards = dg.getActionCards();
		ArrayList<String> actCardNames = new ArrayList<String>();
		for (CardPair cp : actionCards) {
			actCardNames.add(cp.getCard().getName());
		}
		return actCardNames.toArray(new String[0]);
	}

	/**
	 * Broadcast a message to all clients. This method also informs the current
	 * lobby leader of his role and sends him all available action card names (for
	 * the card selection).
	 * 
	 * @author Philip Käppeli
	 * @param message
	 */
	public void broadcast(GameMessage message) {
		boolean lobbyLeaderset = false;
		for (Socket s : ServerLobbyModel.lobbySockets) {
			// Define the lobby leader
			if (!lobbyLeaderset) {
				connector.send(new GameMessage(MessageType.SETLOBBYLEADER, getActionCardNames()), s);
				lobbyLeaderset = true;
			}
			if (!s.isClosed()) {
				connector.send(message, s);
			}
		}
	}

	/**
	 * JavaFX task that manages the further communication with the clients beyond
	 * joining the lobby. Handles chat messages, clients leaving as well as the
	 * lobby leader trying to start a game.
	 * 
	 * @author Philip Käppeli
	 */
	public void waitForMessages() {

		final Task<Void> listenerTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				// Wait for messages from the client
				GameMessage msg;
				do {
					msg = connector.receive(client);
					switch (msg.getType()) {
					case STARTGAME:
						if (connector.isGameRunning()) {
							// Game is running, don't start
							connector.send(new GameMessage(MessageType.GAMERUNNING), client);
						} else {
							// Send gamestart msg to all clients
							broadcast(msg);
							// Pass selected cards and number of players back to the connector
							String[] selCards = msg.getParameters();
							ArrayList<String> selActionCards = new ArrayList<String>();
							for (String cardName : selCards) {
								selActionCards.add(cardName);
							}
							connector.setupGameStart(selActionCards, lobbySockets.size());
						}
						break;
					case CHATMESSAGE:
						broadcast(msg);
						break;
					case ENDOFCONNECTION:
						// Client wants to leave - reflect message
						connector.send(msg, client);
						// Remove name and socket from the lists, close connection
						lobbyNames.remove(lobbySockets.indexOf(client));
						lobbySockets.remove(client);
						client.close();
						// Update lobbylist of other players
						broadcast(new GameMessage(MessageType.UPDATELOBBY,
								ServerLobbyModel.lobbyNames.toArray(new String[0])));
						break;
					default:
						// do nothing
						break;
					}
				} while (msg.getType() != MessageType.ENDOFCONNECTION);
				return null;
			}
		};
		new Thread(listenerTask).start();
	}
}
