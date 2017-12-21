package client;

import client.connectVC.ConnectController;
import client.connectVC.ConnectView;
import client.modelClasses.MenuModel;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * MAIN CLASS CLIENT
 * 
 * @author Pascal Moll
 *
 */
public class ClientMain extends Application {
	protected MenuModel model;
	protected ConnectView cView;

	// Main
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Set up the menu model and start the connect view
	 * 
	 * @author Pascal Moll
	 */
	@Override
	public void start(Stage primaryStage) {
		// MODEL
		model = new MenuModel();

		// CONNECT VC
		cView = new ConnectView(new Stage(), model);	
		new ConnectController(model, cView);
		cView.start();
	}

	/**
	 * If the clients get closed, save changes to the configuration (if there are
	 * any)
	 * 
	 * @author Pascal Moll
	 */
	@Override
	public void stop() {
		// Save config file if it has been loaded already
		if (ServiceLocator.getServiceLocator() != null
				&& ServiceLocator.getServiceLocator().getConfiguration() != null) {
			ServiceLocator.getServiceLocator().getConfiguration().save();
		}
	}
}
