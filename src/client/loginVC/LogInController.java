package client.loginVC;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import client.ServiceLocator;
import client.commonClasses.Translator;
import client.mainMenuVC.MainMenuController;
import client.mainMenuVC.MainMenuView;
import client.modelClasses.MenuModel;
import client.soundeffects.Sound;
import javafx.stage.Stage;
import shared.abstractClasses.Controller;

/**
 * Controller for Login MVC, checks if valid username is written and set username string in model.
 * 
 * @author Pascal Moll
 *
 */

public class LogInController extends Controller<MenuModel, LogInView>{
	private String username;
	private MainMenuView mmView;
	private Translator t;

	/**
	 * Constructor handles Button Events. Handles Incorrect Username and Leads to MainMenu if Username is Correct.
	 * 
	 * @author Pascal Moll
	 * @param model
	 * @param view
	 */
	public LogInController(MenuModel model, LogInView view) {
		super(model, view);

		//Getting Translator
		ServiceLocator sl = ServiceLocator.getServiceLocator();
		this.t = sl.getTranslator();
		
		//Clears TextField if clicked on textfield, after invalid Username is typed in!
		view.txtf_username.setOnMouseClicked((event) -> {
			if(view.txtf_username.getText().equals(t.getString("login.txtf_username"))){
				view.txtf_username.clear();
			}
		});
		
		//Clears TextField if any key is pressed, after invalid Username is typed in!
		view.txtf_username.setOnKeyPressed((event) -> {
			if(view.txtf_username.getText().equals(t.getString("login.txtf_username"))){
				view.txtf_username.clear();
			}
		});
 		
		//Register Button for Event
		view.btn_login.setOnAction((event)-> {	
			Sound.playButton();
			username = view.txtf_username.getText();	
			
			//If checks validation of username
			if(LogInController.testUsername(username)){
				model.setUsername(username);
				mmView = new MainMenuView(new Stage(), model);
				mmView.getStage().setHeight(view.getStage().getHeight());
				mmView.getStage().setWidth(view.getStage().getWidth());
				mmView.getStage().setX(view.getStage().getX());
				mmView.getStage().setY(view.getStage().getY());
				new MainMenuController(model, mmView);
				mmView.start();
				view.stop();
			} else {
				view.txtf_username.setText(t.getString("login.txtf_username"));
			}
			
			
		});
		
		/**
		 * The user can continue with enter
		 * @author User Peter, https://stackoverflow.com/questions/37648222/how-can-i-detect-the-space-keyevent-anywhere-in-my-javafx-app
		 */
		view.txtf_username.setOnKeyReleased((e) -> {
			if(e.getCode().toString() == "ENTER"){
				view.btn_login.fire();
			}
		});
		
	}

	/**
	 * Checks if username has no special signs in it.
	 * And Username has to be between 2 and 14 signs.
	 * 
	 * @author Pascal Moll
	 * @param username
	 * @return
	 */
	private static Boolean testUsername(String username){
		//Setting Up Regex -> All Numbers and Letters maximum 14 signs
		String pattern ="[A-z1-9]{2,14}";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(username);
			
		if(m.matches() && !username.isEmpty()){
			return true;
		} else {
			return false;
		}
	}
}
