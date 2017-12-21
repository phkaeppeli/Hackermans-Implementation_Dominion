package server.modelClasses;

import java.net.Socket;
import java.util.ArrayList;

import server.database.DataGrabber;
import server.resourceClasses.ServerConnector;
import shared.resourceClasses.GameMessage;
import shared.resourceClasses.MessageType;

/**
 * This class manages the communication between clients who want to see the
 * highscore list and the server.
 * 
 * @author Philip Käppeli
 *
 */
public class ServerHighscoreModel {

	/**
	 * 
	 * CONSTRUCTOR: Gets the highscore list from the data base grabber, converts it
	 * to a string array and sends it to the client. Ends the connection right after
	 * this, as no further communication is neccessary.
	 * 
	 * @author Philip Käppeli
	 * @param connector
	 * @param client
	 */
	public ServerHighscoreModel(ServerConnector connector, Socket client) {
		// Get highscore list from the db
		ArrayList<String> highscoreList = new DataGrabber().getHighscore();

		// Send it to the client
		connector.send(new GameMessage(MessageType.GETHIGHSCORES, highscoreList.toArray(new String[0])), client);

		// End connection
		connector.send(new GameMessage(MessageType.ENDOFCONNECTION), client);
		try {
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
