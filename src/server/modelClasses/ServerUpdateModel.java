package server.modelClasses;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

import client.ServiceLocator;
import javafx.concurrent.Task;
import server.database.DataGrabber;
import server.resourceClasses.ServerConnector;
import shared.resourceClasses.GameMessage;
import shared.resourceClasses.MessageType;

/**
 * 
 * @author Philipp Lehmann
 *
 */
public class ServerUpdateModel {
	/**
	 * CONSTRUCTOR
	 * 
	 * @author Philipp Lehmann
	 * @param connector
	 * @param client
	 *
	 */
	public ServerUpdateModel(ServerConnector connector, Socket client) {
		createUpdateTask(connector, client);
	}

	/**
	 * Receives version number from client.
	 * Compares version number of database with version number of client.
	 * Sends images to client if version IDs differ from each other.
	 *  
	 * @author Philipp Lehmann
	 * @param connector
	 * @param client
	 * 
	 */
	public void createUpdateTask(ServerConnector connector, Socket client) {
		Task<Void> updateTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				// Get client version id
				GameMessage versionMsg = connector.receive(client);

				// Get DB version id from local cfg file
				DataGrabber dg = new DataGrabber();

				// Compare
				if (versionMsg.getParameters()[0].equals(dg.getVersion())) {
					connector.send(new GameMessage(MessageType.VERSIONOK, dg.getVersion()), client);
					connector.send(new GameMessage(MessageType.ENDOFCONNECTION), client);
					client.close();
				} else {
					// If different, send db data to client
					connector.send(new GameMessage(MessageType.OLDVERSION), client);
					for (Locale locale : ServiceLocator.getServiceLocator().getLocales()) {
						// Get images for each language
						ArrayList<File> images = dg.getAllCardImages(locale.getLanguage());
						// Send them to the client
						connector.send(new GameMessage(MessageType.UPDATEFILES, images, locale.getLanguage()), client);
					}

					// send version ok message
					connector.send(new GameMessage(MessageType.VERSIONOK, dg.getVersion()), client);
					connector.send(new GameMessage(MessageType.ENDOFCONNECTION), client);
					client.close();
				}
				// End connection
				return null;
			}
		};
		new Thread(updateTask).start();
	}
}
