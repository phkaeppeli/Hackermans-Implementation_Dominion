package client.modelClasses;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import client.ServiceLocator;
import client.commonClasses.Configuration;
import client.commonClasses.Translator;
import client.resourceClasses.ClientConnector;
import client.resourceClasses.ListEntry;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import shared.abstractClasses.Model;
import shared.resourceClasses.MessageType;

/**
 * Model class used for every mvc except for the game.
 * 
 * @author Philip Käppeli
 * @author Philipp Lehmann
 *
 */
public class MenuModel extends Model {
	// GENERAL
	protected ServiceLocator serviceLocator;
	// CONNECTION
	protected int port;
	protected String hostname;
	protected ClientConnector client;
	public IntegerProperty connectionAccepted;
	// SPLASH
	public BooleanProperty isVersionOk;
	// HIGHSCORES
	public ObservableList<ListEntry> highscoreList;
	// LOBBY
	public ObservableList<ListEntry> lobbyList;
	public ObservableList<String> chatMessages;
	public BooleanProperty isLobbyLeader;
	public BooleanProperty isLobbyReady;
	protected String[] availActionCards; // PHILIPP: use this
	protected String[] selActionCards; // PHILIPP: use this
	public BooleanProperty startGame;
	protected String username;
	public StringProperty gameRunningMsg;
	public StringProperty lobbyFullMsg;

	/**
	 * CONSTRUCTOR: sets up various variables
	 * 
	 * @author Philip Käppeli
	 */
	public MenuModel() {
		// CONNECT
		setUsername("");
		connectionAccepted = new SimpleIntegerProperty();
		connectionAccepted.set(0);
		// SPLASH
		isVersionOk = new SimpleBooleanProperty();
		isVersionOk.set(false);
		// HIGHSCORES
		highscoreList = FXCollections.observableArrayList();
		lobbyFullMsg = new SimpleStringProperty();
		lobbyFullMsg.set("");
		// LOBBY
		setupLobbyVars();
	}

	// SPLASHSCREEN: Start initializer thread
	public void initialize() {
		new Thread(initializer).start();
	}

	/**
	 * SPLASHSCREEN: Set up initializer - @author Brad Richards A task is a JavaFX
	 * class that implements Runnable. Tasks are designed to have attached
	 * listeners, which we can use to monitor their progress.
	 */
	public final Task<Void> initializer = new Task<Void>() {
		@Override
		protected Void call() throws Exception {
			this.updateProgress(1, 5);

			// Create the service locator to hold our resources
			serviceLocator = ServiceLocator.getServiceLocator();
			this.updateProgress(2, 5);

			// Initialize the resources in the service locator
			serviceLocator.setConfiguration(new Configuration());
			this.updateProgress(3, 5);

			String language = serviceLocator.getConfiguration().getOption("Language");
			serviceLocator.setTranslator(new Translator(language));
			this.updateProgress(4, 5);

			/**
			 * Send version number to server and wait for the updates to finish
			 * 
			 * @author Philipp Lehmann
			 * 
			 **/
			String version = serviceLocator.getConfiguration().getOption("Version");
			if (version == null) {
				version = "0.0";
			}
			getClient().sendMessage(MessageType.CHECKVERSION, version);
			isVersionOk.addListener((c) -> {
				if (isVersionOk.get()) {
					// start login
					this.updateProgress(5, 5);
				}
			});
			return null;
		}
	};

	/**
	 * SPLASHSCREEN: update local cfg file with new Version number
	 * 
	 * @author Philipp Lehmann
	 * 
	 **/
	public void updateVersionNum(String newVersion) {
		serviceLocator = ServiceLocator.getServiceLocator();
		serviceLocator.getConfiguration().setLocalOption("Version", newVersion);
	}

	/**
	 * SPLASHSCREEN: update image files in user directory
	 * 
	 * 
	 * @author Philipp Lehmann
	 * @param files
	 * @param locale
	 * 
	 **/
	public void updateFiles(ArrayList<File> files, String locale) {
		// Create folder
		String path = System.getProperty("user.dir").replace("\\", "/");
		String fullPath = path + "/imagesClient/" + locale;
		File folder = new File(fullPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		// Fill folder with the files from the server
		for (File f : files) {
			try {
				// Source: stackoverflow.com/questions/16433915
				Files.copy(f.toPath(), (new File(fullPath + "/" + f.getName()).toPath()),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * LOBBY: Initialize observable values. This is needed to avoid mistakes/wrong
	 * actions when a client leaves and re-joins a lobby
	 * 
	 * @author Philip Käppeli
	 */
	public void setupLobbyVars() {
		if (lobbyList == null) {
			lobbyList = FXCollections.observableArrayList();
		}
		isLobbyLeader = new SimpleBooleanProperty();
		isLobbyLeader.set(false);
		isLobbyReady = new SimpleBooleanProperty();
		isLobbyReady.set(false);
		startGame = new SimpleBooleanProperty();
		startGame.set(false);
		gameRunningMsg = new SimpleStringProperty();
		chatMessages = FXCollections.observableArrayList();
	}

	/**
	 * LOBBY: Update lobbylist after a new client has joined
	 * 
	 * @author Philip Käppeli
	 * @param entries
	 */
	public void updateLobbyList(String[] entries) {
		// If this method gets called before the constructor(threads!), set here
		if (lobbyList == null) {
			lobbyList = FXCollections.observableArrayList();
		}

		Platform.runLater(() -> {
			// Clear list and re-write it
			lobbyList.clear();
			boolean leaderSet = false;
			String roleText;
			for (String s : entries) {
				// Define leader
				if (!leaderSet) {
					roleText = "Leader";
					leaderSet = true;
				} else {
					roleText = "Player";
				}
				// Build list
				lobbyList.add(new ListEntry(s, roleText));
			}
		});
	}

	// LOBBY: send start game message
	/**
	 * LOBBY: send start game message
	 * 
	 * @author Philipp Lehmann
	 */
	public void startGame() {
		if(selActionCards == null){
			this.getClient().sendMessage(MessageType.STARTGAME, availActionCards);
		}else{
			this.getClient().sendMessage(MessageType.STARTGAME, selActionCards);
		}
	}

	/**
	 * LOBBY: client left
	 * 
	 * @author Philip Käppeli
	 */
	public void disconnectClient() {
		// Disconnect socket
		setupLobbyVars();
		getClient().disconnect();
	}

	/**
	 * HIGHSCORELIST: Update highscorelist with information from client
	 * 
	 * @author Philip Käppeli
	 * @param highscores
	 */
	public void updateHighscoreList(String[] highscores) {
		Platform.runLater(() -> {
			int rank = 1;
			// Go through all entries
			for (String entry : highscores) {
				// Split entry into name and score
				String[] entrySplit = entry.split(";");
				String name = entrySplit[0];
				String score = entrySplit[1];
				// Add entry + rank number to observable list
				highscoreList.add(new ListEntry(Integer.toString(rank), name, score));
				rank++;
			}
		});
	}

	/**
	 * GETTERS and SETTERS
	 */
	public void setLobbyLeader() {
		isLobbyLeader.set(true);
	}

	public void setAvailActionCards(String[] actCardNames) {
		this.availActionCards = actCardNames;
	}

	public String[] getAvailActionCards() {
		return this.availActionCards;
	}
	
	public void setSelActionCards(String[] actCardNames) {
		this.selActionCards = actCardNames;
	}

	public String[] getSelActionCards() {
		return this.selActionCards;
	}


	public void setConnector(ClientConnector client) {
		this.setClient(client);
	}

	public ClientConnector getClient() {
		return client;
	}

	public void setClient(ClientConnector client) {
		this.client = client;
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
}
