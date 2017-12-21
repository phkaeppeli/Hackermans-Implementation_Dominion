package client.splashVC;

import client.loginVC.LogInController;
import client.loginVC.LogInView;
import client.modelClasses.MenuModel;
import client.resourceClasses.ClientConnector;
import javafx.concurrent.Worker;
import javafx.stage.Stage;
import shared.abstractClasses.Controller;
import shared.resourceClasses.MessageType;

/**
 * Controller for Splash MVC. Handles client connetion and progressbar.
 * 
 * @author Philipp Lehmann
 *
 */
public class SplashController extends Controller<MenuModel, SplashView> {
	private LogInView lgview;

	/**
	 * CONSTRUCTOR
	 * 
	 * @author Philipp Lehmann
	 *
	 */
	public SplashController(MenuModel model, SplashView view) {
		super(model, view);
		
		// Build client connection
		ClientConnector client = new ClientConnector();
		client.createConnection(MessageType.UPDATECONNECTION, model);
		model.setClient(client);
		 
		// bind progressbar to the initializer task
		view.pb_progress.progressProperty().bind(model.initializer.progressProperty());

		// Tell the main to continue, if state is succeeded
		model.initializer.stateProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == Worker.State.SUCCEEDED) {
				lgview = new LogInView(new Stage(), model);
				new LogInController(model, lgview);
				lgview.getStage().setHeight(view.getStage().getHeight());
				lgview.getStage().setWidth(view.getStage().getWidth());
				lgview.getStage().setX(view.getStage().getX());
				lgview.getStage().setY(view.getStage().getY());
				lgview.start();
				view.stop();
			}
		});
	}

}
