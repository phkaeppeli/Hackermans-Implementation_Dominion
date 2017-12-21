package server;

import javafx.application.Application;
import javafx.stage.Stage;
import server.resourceClasses.ServerConnector;

/**
 * MAIN CLASS SERVER
 * 
 * @author Philip Käppeli
 *
 */
public class ServerMain extends Application {
	protected static ServerConnector server;

	// Main
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Start up the server connector to allow clients to connect with the server.
	 * 
	 * @author Philip Käppeli
	 */
	@Override
	public void start(Stage primaryStage) {
		int port = 1116;
		server = new ServerConnector();
		server.serveContent(port);

	}

	/**
	 * If the gui gets closed, the server socket should also be closed.
	 * 
	 * @author Philip Käppeliss
	 */
	@Override
	public void stop() {
		if (!server.getServerSocket().isClosed()) {
			server.closeSocket();
		}
	}
}
