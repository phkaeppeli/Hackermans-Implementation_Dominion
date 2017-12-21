package client.settingsVC;

import client.ServiceLocator;
import client.commonClasses.Translator;
import client.mainMenuVC.MainMenuController;
import client.mainMenuVC.MainMenuView;
import client.modelClasses.MenuModel;
import client.soundeffects.Sound;
import javafx.stage.Stage;
import shared.abstractClasses.Controller;

/**
 * Controller for Settings MVC. Handles Change of Language and Music/SoundFX.
 * 
 * @author Pascal Moll
 *
 */

public class SettingsController extends Controller<MenuModel, SettingsView> {

	private MainMenuView mmView;
	private Translator t;
	private static ServiceLocator s1;

	/**
	 * Constructor: 
	 * Checks if English or German language is activated.
	 * Sets Back Button on Action -> Leads to MainMenu.
	 * Sets Language Buttons on Action -> updates all Texts and sets new Translator language
	 * Sets Music Button on Action -> Stops or Plays Music
	 * Sets Sound Button on Action -> Stops or Plays Button Sound.
	 * 
	 * @author Pascal Moll
	 * @param model
	 * @param view
	 */
	public SettingsController(MenuModel model, SettingsView view) {
		super(model, view);
		//Getting Translator
		s1 = ServiceLocator.getServiceLocator();
		t = s1.getTranslator();

		// Disable the togglebutton of the activated language
		if (t.getCurrentLocale().toString().equals("en"))
			view.tbtn_english.setDisable(true);
		else
			view.tbtn_german.setDisable(true);

		// Register Back Button for Event -> Leads to MainMenu
		view.btn_back.setOnAction((event) -> {
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

		// Register English button for event
		view.tbtn_english.setOnAction((event) -> {
			Sound.playButton();

			// Set new language
			t = new Translator("en");
			s1.setTranslator(t);

			// Set new Localoption in Configuration, to recognize the language.
			s1.getConfiguration().setLocalOption("Language", "en");

			// Set Labels on Settings Page to english.
			view.lbl_language.setText(t.getString("settings.lbl_language"));
			view.lbl_sound.setText(t.getString("settings.lbl_music"));
			view.lblTitle.setText(t.getString("settings.lbl_settings"));
			view.tbtn_english.setText(t.getString("settings.tbtn_eng"));
			view.tbtn_german.setText(t.getString("settings.tbtn_de"));
			view.btn_back.setText(t.getString("settings.btn_back"));

			if (Sound.playBackgroundSound)
				view.tbtn_music.setText(t.getString("settings.tbtn_music_on"));
			else
				view.tbtn_music.setText(t.getString("settings.tbtn_music_off"));
			if (Sound.playButtonSound)
				view.tbtn_soundfx.setText(t.getString("settings.tbtn_soundfx_on"));
			else
				view.tbtn_soundfx.setText(t.getString("settings.tbtn_soundfx_off"));

			//Disable  Button of activated Language
			view.tbtn_english.setDisable(true);
			view.tbtn_german.setDisable(false);
		});

		// Register German button for event
		view.tbtn_german.setOnAction((event) -> {
			Sound.playButton();

			// Change Language
			t = new Translator("de");
			s1.setTranslator(t);

			// Set new Localoption in Configuration, to recognize the language.
			s1.getConfiguration().setLocalOption("Language", "de");

			// Set Labels on Settings Page to german.
			view.lbl_language.setText(t.getString("settings.lbl_language"));
			view.lbl_sound.setText(t.getString("settings.lbl_music"));
			view.lblTitle.setText(t.getString("settings.lbl_settings"));
			view.tbtn_english.setText(t.getString("settings.tbtn_eng"));
			view.tbtn_german.setText(t.getString("settings.tbtn_de"));
			view.btn_back.setText(t.getString("settings.btn_back"));

			if (Sound.playBackgroundSound) {
				view.tbtn_music.setText(t.getString("settings.tbtn_music_on"));
			} else {
				view.tbtn_music.setText(t.getString("settings.tbtn_music_off"));
			}
			if (Sound.playButtonSound) {
				view.tbtn_soundfx.setText(t.getString("settings.tbtn_soundfx_on"));
			} else {
				view.tbtn_soundfx.setText(t.getString("settings.tbtn_soundfx_off"));
			}

			//Disable  Button of activated Language
			view.tbtn_english.setDisable(false);
			view.tbtn_german.setDisable(true);

		});

		//Register Music Button
		view.tbtn_music.setOnAction((event) -> {
			Sound.playButton();

			//If-Else checks whether music is playing or not. 
			if (Sound.playBackgroundSound) {
				if (Sound.player != null) {
					Sound.player.stop();
				}
				Sound.playBackgroundSound = false;
				view.tbtn_music.setText(t.getString("settings.tbtn_music_off"));
				s1.getConfiguration().setLocalOption("Music", "off");
			} else {
				Sound.playBackgroundSound = true;
				Sound.playSong();
				view.tbtn_music.setText(t.getString("settings.tbtn_music_on"));
				s1.getConfiguration().setLocalOption("Music", "on");
			}
		});

		//Register SoundFx Button
		view.tbtn_soundfx.setOnAction((event) -> {
			Sound.playButton();
			
			//If - Else checks whether soundfx is activated or not.
			if (Sound.playButtonSound) {
				Sound.playButtonSound = false;
				view.tbtn_soundfx.setText(t.getString("settings.tbtn_soundfx_off"));
				s1.getConfiguration().setLocalOption("SoundFX", "off");
				;
			} else {
				Sound.playButtonSound = true;
				view.tbtn_soundfx.setText(t.getString("settings.tbtn_soundfx_on"));
				s1.getConfiguration().setLocalOption("SoundFX", "on");
				;
			}
		});

	}
}
