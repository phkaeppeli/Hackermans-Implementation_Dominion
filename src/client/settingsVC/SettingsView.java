package client.settingsVC;

import client.ServiceLocator;
import client.commonClasses.Translator;
import client.modelClasses.MenuModel;
import client.soundeffects.Sound;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import shared.abstractClasses.View;

/**
 * View for Settings MVC. Contains 4 Toggle Buttons (Englisch, German, Music, SoundFx) and Back Button to MainMenu
 * 
 * @author Pascal Moll
 *
 */

public class SettingsView extends View<MenuModel> {

	// Defining all Instance Variables
	protected Label lblTitle;
	protected Label lbl_language;
	protected Label lbl_sound;
	protected ToggleButton tbtn_english;
	protected ToggleButton tbtn_german;
	protected ToggleButton tbtn_music;
	protected ToggleButton tbtn_soundfx;
	protected Button btn_back;
	protected final double FILL = 1.0;
	protected DoubleProperty fontSizeBase15;
	protected DoubleProperty fontSizeBase20;
	/**
	 * Constructor: Default
	 * @param stage
	 * @param model
	 */
	public SettingsView(Stage stage, MenuModel model) {
		super(stage, model);
	}
	/**
	 * Create and Show Settings GUI. Contains ToggleButtons for Language, Music and Sound. Back Button
	 * 
	 * @author Pascal Moll
	 */
	@Override
	protected Scene create_GUI() {
		fontSizeBase15 = new SimpleDoubleProperty();
		fontSizeBase20 = new SimpleDoubleProperty();
		
		//Getting Translator
		ServiceLocator sl = ServiceLocator.getServiceLocator();
		Translator t = sl.getTranslator();
		
		// Setting up Panes
		BorderPane root = new BorderPane();
		BorderPane pane_inner = new BorderPane();
		bindElement(pane_inner, root, 0.3, 0.4);
		GridPane gridpane = new GridPane();
		bindElement(gridpane, pane_inner, FILL, 0.7);
		HBox hbox_title = new HBox();
		bindElement(hbox_title, pane_inner, 0.9, 0.2);
		HBox hbox_buttons = new HBox();
		bindElement(hbox_buttons, pane_inner, FILL, 0.1);
		
		
		//Setting Up Labels, ToggleButtons, Buttons
		this.lblTitle = new Label(t.getString("settings.lbl_settings"));
		bindElement(lblTitle, hbox_title, FILL, FILL);
		this.btn_back = new Button(t.getString("settings.btn_back"));
		bindElement(btn_back, hbox_buttons, 0.2, 1.1);
		this.lbl_language = new Label(t.getString("settings.lbl_language"));
		bindElement(lbl_language, gridpane, 0.2, 0.2);
		this.tbtn_german = new ToggleButton(t.getString("settings.tbtn_de"));
		bindElement(tbtn_german, gridpane, 0.3, 0.15);
		this.tbtn_english = new ToggleButton(t.getString("settings.tbtn_eng"));
		bindElement(tbtn_english, gridpane, 0.3, 0.15);
		this.lbl_sound = new Label(t.getString("settings.lbl_music"));
		bindElement(lbl_sound, gridpane, 0.2, 0.2);
		this.tbtn_music = new ToggleButton();
		bindElement(tbtn_music, gridpane, 0.3, 0.15);
		this.tbtn_soundfx = new ToggleButton();
		bindElement(tbtn_soundfx, gridpane, 0.3, 0.15);

		//Richtige Bezeichnung bei Media Togglebuttons
		if(Sound.playBackgroundSound)
			this.tbtn_music.setText(t.getString("settings.tbtn_music_on"));
		else
			this.tbtn_music.setText(t.getString("settings.tbtn_music_off"));
		if(Sound.playButtonSound)
			this.tbtn_soundfx.setText(t.getString("settings.tbtn_soundfx_on"));
		else
			this.tbtn_soundfx.setText(t.getString("settings.tbtn_soundfx_off"));
		
		//Setting up Borderpane root for dynamic Handling
		root.setCenter(pane_inner);
		root.minWidthProperty().bind(stage.widthProperty());
		root.maxWidthProperty().bind(stage.widthProperty());
		root.minHeightProperty().bind(stage.minHeightProperty());
		root.maxHeightProperty().bind(stage.minHeightProperty());
		
		//Setting up pane_inner with all panes
		pane_inner.setTop(hbox_title);
		pane_inner.setCenter(gridpane);
		pane_inner.setBottom(hbox_buttons);
		
		//Setting up gridpane
		gridpane.add(lbl_language, 1, 0);
		gridpane.add(tbtn_english, 2, 0);
		gridpane.add(tbtn_german, 3, 0);
		gridpane.add(lbl_sound, 1, 2);
		gridpane.add(tbtn_music, 2, 2);
		gridpane.add(tbtn_soundfx, 3, 2);
		
		//Setting up Title Hbox
		hbox_title.getChildren().add(lblTitle);
		
		//Setting up Bottom - Buttons HBox
		hbox_buttons.getChildren().addAll(btn_back);
		
		// Set ID for CSS
		root.setId("root_Settings");
		pane_inner.setId("pane_inner");
		hbox_title.setId("hbox_title");
		gridpane.setId("gridpane_inside");
		this.btn_back.setId("Button");
		this.lblTitle.setId("lblTitle");
		this.lbl_language.setId("lbl_settings");
		this.tbtn_english.setId("ToggleButton");
		this.tbtn_german.setId("ToggleButton");
		this.tbtn_music.setId("ToggleButton");
		this.tbtn_soundfx.setId("ToggleButton");
		this.lbl_sound.setId("lbl_settings");

		// Setting up primaryStage
		stage.setTitle("Dominion");
		
		
		Scene sc = new Scene(root);
		stage.setScene(sc);
		sc.getStylesheets().add(getClass().getResource("/client/layout/all.css").toExternalForm());
		bindTexts(sc);
		return sc;
	}

	/**
	 * Bind text size to current window size
	 * 
	 * @author Philip Käppeli
	 * @param sc
	 */
	private void bindTexts(Scene sc) {
		// Source: stackoverflow.com/questions/23705654
		// Source: docs.oracle.com/javafx/2/api/javafx/stage/Screen.html
		double screenwidth = Screen.getPrimary().getVisualBounds().getWidth();
		double screenheight = Screen.getPrimary().getVisualBounds().getHeight();

		fontSizeBase15
				.bind((sc.widthProperty().add(sc.heightProperty())).divide(screenwidth + screenheight).multiply(15));
		lbl_language.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		lbl_sound.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		tbtn_english.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		tbtn_german.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		tbtn_music.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		tbtn_soundfx.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		btn_back.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));

		fontSizeBase20
				.bind((sc.widthProperty().add(sc.heightProperty())).divide(screenwidth + screenheight).multiply(20));
		lblTitle.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase20.asString()));
	}
	
	/**
	 * Bind elements for dynamic resizing
	 * 
	 * @author Philip Käppeli
	 * @param child
	 * @param parent
	 * @param widthMulti
	 * @param heightMulti
	 */
	private void bindElement(Region child, Region parent, double widthMulti, double heightMulti) {
		child.minWidthProperty().bind(parent.widthProperty().multiply(widthMulti));
		child.maxWidthProperty().bind(parent.widthProperty().multiply(widthMulti));
		child.minHeightProperty().bind(parent.heightProperty().multiply(heightMulti));
		child.maxHeightProperty().bind(parent.heightProperty().multiply(heightMulti));
	}
	
	public Button getBtnBack(){
		return this.btn_back;
	}
	
	public ToggleButton getTbtnGerman(){
		return this.tbtn_german;
	}
	
	public ToggleButton getTbtnEnglish(){
		return this.tbtn_english;
	}
}
