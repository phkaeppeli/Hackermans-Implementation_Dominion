package client.mainMenuVC;

import client.highScoresVC.HighScoreController;
import client.highScoresVC.HighScoreView;
import client.lobbyVC.LobbyController;
import client.lobbyVC.LobbyView;
import client.modelClasses.MenuModel;
import client.resourceClasses.ClientConnector;
import client.settingsVC.SettingsController;
import client.settingsVC.SettingsView;
import client.soundeffects.Sound;
import javafx.stage.Stage;
import shared.abstractClasses.Controller;
import shared.resourceClasses.MessageType;

/**
 * Controller for MainMenu MVC, Can lead to different other gui's, center of Menu.
 * 
 * @author Pascal Moll
 *
 */

public class MainMenuController extends Controller<MenuModel, MainMenuView>{

	private HighScoreView hsview;
	private SettingsView stview;
	private LobbyView lbview;

	/**
	 * Constructor: Handles all three different Buttons and where they have to lead to.
	 * 
	 * @author Pascal Moll
	 * @param model
	 * @param view
	 */
	public MainMenuController(MenuModel model, MainMenuView view) {		
		super(model, view);
		
		//Register Button for Event -> leads to Highscore List
		view.btn_highscore.setOnAction((event) -> {
			Sound.playButton();
			hsview = new HighScoreView(new Stage(), model);
			new HighScoreController(model, hsview);
			hsview.getStage().setHeight(view.getStage().getHeight());
			hsview.getStage().setWidth(view.getStage().getWidth());
			hsview.getStage().setX(view.getStage().getX());
			hsview.getStage().setY(view.getStage().getY());
			hsview.start();
			ClientConnector client = new ClientConnector();
			client.createConnection(MessageType.HIGHSCORECONNECTION, model);
			view.stop();
		});
		
		//Register Button for Event -> Leads to Lobby
		view.btn_lobby.setOnAction((event) -> {
			Sound.playButton();
			lbview = new LobbyView(new Stage(), model);
			lbview.getStage().setHeight(view.getStage().getHeight());
			lbview.getStage().setWidth(view.getStage().getWidth());
			lbview.getStage().setX(view.getStage().getX());
			lbview.getStage().setY(view.getStage().getY());
			new LobbyController(model, lbview);
			lbview.start();
			ClientConnector client = new ClientConnector();
			client.createConnection(MessageType.LOBBYCONNECTION,model);
			model.setConnector(client);
			view.stop();
			
		});
		
		//Register Button for Event -> Leads to Settings
		view.btn_settings.setOnAction((event) -> {
			Sound.playButton();
			stview = new SettingsView(new Stage(), model);
			stview.getStage().setHeight(view.getStage().getHeight());
			stview.getStage().setWidth(view.getStage().getWidth());
			stview.getStage().setX(view.getStage().getX());
			stview.getStage().setY(view.getStage().getY());
			new SettingsController(model, stview);
			stview.start();
			view.stop();
		});
	}


	
}