package client.highScoresVC;

import client.mainMenuVC.MainMenuController;
import client.mainMenuVC.MainMenuView;
import client.modelClasses.MenuModel;
import client.soundeffects.Sound;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.stage.Stage;
import shared.abstractClasses.Controller;

/**
 * Controller for HighScore MVC. Listens for changes in HighScore List
 * and adds items to table, if necessary.
 * 
 * @author Philipp Lehmann
 *
 */
public class HighScoreController extends Controller<MenuModel, HighScoreView> {
	private MainMenuView mmView;
	
	/**
	 * CONSTRUCTOR:
	 * 
	 * @author Philipp Lehmann
	 *
	 */
	public HighScoreController(MenuModel model, HighScoreView view) {
		super(model, view);
		
		// Empty highscorelist on closing
		view.getStage().setOnHidden((e) -> {
			model.highscoreList.clear();
		});

		// Change listener for the highscore list
		model.highscoreList.addListener(new ListChangeListener<Object>() {
			@Override
			public void onChanged(Change<?> c) {
				Platform.runLater(() -> {
					if (model.highscoreList != null) {
						view.table.setItems(model.highscoreList);
					}
				});
			}
		});
		
		/**
		 * Add ActionHandler for back button
		 */
		view.btnBack.setOnAction((event) -> {
			Sound.playButton();
			mmView = new MainMenuView(new Stage(), model);
			mmView.getStage().setHeight(view.getStage().getHeight());
			mmView.getStage().setWidth(view.getStage().getWidth());
			mmView.getStage().setX(view.getStage().getX());
			mmView.getStage().setY(view.getStage().getY());
			new MainMenuController(model, mmView);
			mmView.start();
			view.stop();
		});
	}
}
