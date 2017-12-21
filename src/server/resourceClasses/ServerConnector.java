package server.resourceClasses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.concurrent.Task;
import server.ServerGUI;
import server.modelClasses.ServerGameModel;
import server.modelClasses.ServerHighscoreModel;
import server.modelClasses.ServerLobbyModel;
import server.modelClasses.ServerUpdateModel;
import shared.resourceClasses.GameMessage;

/**
 * This class is the central point used for communication between the server and
 * clients. All clients that connect and messages the server sends/receives pass
 * through here.
 * 
 * @author Philip Käppeli
 *
 */
public class ServerConnector {
	protected ServerConnector thisObject;
	protected ServerSocket serverSocket;
	protected int port;
	protected static ServerGameModel gameModel;
	protected static ArrayList<String> selActionCards;
	protected static int numPlayers;
	protected boolean isGameRunning;
	protected ServerGUI gui;

	/**
	 * JavaFX task for the server. Handles all clients connecting to the server and
	 * redirects further communication to the according server model classes.
	 * 
	 * @author Philip Käppeli
	 */
	final Task<Void> serverTask = new Task<Void>() {
		@Override
		protected Void call() throws Exception {

			// Create server socket
			serverSocket = new ServerSocket(port);

			// Accept clients endlessly
			while (!getServerSocket().isClosed()) {
				// Accept a new client
				Socket clientSocket = getServerSocket().accept();

				// Check connection type
				GameMessage msg = receive(clientSocket);
				switch (msg.getType()) {
				// The client is asking for db updates
				case UPDATECONNECTION:
					new ServerUpdateModel(thisObject, clientSocket);
					break;
				// The client wants to see the highscore list
				case HIGHSCORECONNECTION:
					new ServerHighscoreModel(thisObject, clientSocket);
					break;
				// The client wants to join a lobby
				case LOBBYCONNECTION:
					new ServerLobbyModel(thisObject, clientSocket, msg.getType(), msg.getParameters()[0]);
					break;
				// The client wants to join a game
				case GAMECONNECTION:
					if (!isGameRunning) {
						isGameRunning = true;
						gameModel = new ServerGameModel(thisObject, numPlayers, selActionCards);
					}
					gameModel.addPlayer(new Player(msg.getParameters()[0], clientSocket));
					break;
				default:
					// do nothing
					break;
				}
			}
			return null;
		}
	};

	/**
	 * Receive a message from a certain client socket and log it in the server gui.
	 * 
	 * @author Philip Käppeli
	 * @param client
	 * @return GameMessage that was received
	 */
	public GameMessage receive(Socket client) {
		GameMessage msg = GameMessage.receive(client);
		// log
		Platform.runLater(() -> {
			gui.logMessage(msg.toString(), "Receive: ", client.getInetAddress().toString());
		});
		return msg;
	}

	/**
	 * Send a message to a certain client socket
	 * 
	 * @author Philip Käppeli
	 * @param msg
	 * @param client
	 */
	public void send(GameMessage msg, Socket client) {
		msg.send(client);
		// log
		Platform.runLater(() -> {
			gui.logMessage(msg.toString(), "Send: ", client.getInetAddress().toString());
		});
	}

	/**
	 * This method gets called from the lobby-model to prepare the needed
	 * information for passing it on to the game model when the game starts
	 * 
	 * @author Philip Käppeli
	 * @param selectedActionCardNames
	 * @param numP
	 */
	public void setupGameStart(ArrayList<String> selectedActionCardNames, int numP) {
		selActionCards = selectedActionCardNames;
		numPlayers = numP;
	}

	/**
	 * Initialize the server thread on a certain port
	 * 
	 * @author Philip Käppeli
	 * @param port
	 */
	public void serveContent(int port) {
		thisObject = this;
		this.port = port;
		selActionCards = new ArrayList<String>();
		numPlayers = 0;
		isGameRunning = false;
		// source: stackoverflow.com/questions/2939218
		// code to get the public ip of the server -> enter this in the client
		// connect view to connect to the server
		String ip = "";
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
				ip = in.readLine();
				//In case the amazonaws IP service is not reachable
			} catch (UnknownHostException e) {
				System.out.println("in catch");
				Platform.runLater(() -> {
					gui.logMessage("Amazon IP service unreachable", "ERROR", "");
				});
				
			}
			finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Start gui
		gui = new ServerGUI(ip, port);
		new Thread(serverTask).start();
	}

	/**
	 * Close server socket when the server gui is closed
	 * 
	 * @author Philip Käppeli
	 */
	public void closeSocket() {
		try {
			// Stop server socket
			getServerSocket().close();
		} catch (Exception e) {
			// do nothing
		}
	}

	// GETTER: server socket object
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	// GETTER: is a game running?
	public boolean isGameRunning() {
		return isGameRunning;
	}

	// SETTER: is a game running?
	public void setGameRunning(boolean isGameRunning) {
		this.isGameRunning = isGameRunning;
	}
}
