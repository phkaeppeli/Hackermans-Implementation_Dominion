package client.mainMenuVC;

import client.ServiceLocator;
import client.commonClasses.Translator;
import client.modelClasses.MenuModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import shared.abstractClasses.View;

/**
 * View for MainMenu MVC. Has three Buttons to Lead to Highscore, Settings, Lobby.
 * 
 * @author Pascal Moll
 *
 */

public class MainMenuView extends View<MenuModel> {

	// Defining all Instance Variables
	protected Label lblTitle;
	protected Button btn_lobby;
	protected Button btn_highscore;
	protected Button btn_settings;
	protected final double FILL = 1.0;
	protected DoubleProperty fontSizeBase15;
	protected DoubleProperty fontSizeBase20;

	/**
	 * Constructor: Default
	 * @param stage
	 * @param model
	 */
	public MainMenuView(Stage stage, MenuModel model) {
		super(stage, model);
	}
	/**
	 * Create and show the MainMenu GUI. Contains three Buttons to Lead to Highscore, Settings and Lobby.
	 * 
	 * @author Pascal Moll
	 */
	@Override
	protected Scene create_GUI() {
		fontSizeBase15 = new SimpleDoubleProperty();
		fontSizeBase20 = new SimpleDoubleProperty();

		// Getting Translator
		ServiceLocator sl = ServiceLocator.getServiceLocator();
		Translator t = sl.getTranslator();

		// Setting up Panesnstance Variables and new Variables
		BorderPane root = new BorderPane();
		BorderPane pane_inner = new BorderPane();
		bindElement(pane_inner, root, 0.3, 0.4);
		HBox hbox_title = new HBox();
		bindElement(hbox_title, pane_inner, 0.9, 0.2);
		VBox vbox_buttons = new VBox();
		bindElement(vbox_buttons, pane_inner, FILL, 0.7);

		// Setting up Labels and Buttons
		this.lblTitle = new Label(t.getString("mainmenu.lbl_mainmenu"));
		bindElement(lblTitle, hbox_title, FILL, FILL);
		this.btn_lobby = new Button(t.getString("mainmenu.btn_lobby"));
		bindElement(btn_lobby, vbox_buttons, 0.5, 0.15);
		this.btn_highscore = new Button(t.getString("mainmenu.btn_highscore"));
		bindElement(btn_highscore, vbox_buttons, 0.5, 0.15);
		this.btn_settings = new Button(t.getString("mainmenu.btn_settings"));
		bindElement(btn_settings, vbox_buttons, 0.5, 0.15);

		// Setting up Borderpane root for dynamic handling
		root.setCenter(pane_inner);
		root.minWidthProperty().bind(stage.widthProperty());
		root.maxWidthProperty().bind(stage.widthProperty());
		root.minHeightProperty().bind(stage.minHeightProperty());
		root.maxHeightProperty().bind(stage.minHeightProperty());

		// Setting up pane_inner with all panes
		pane_inner.setTop(hbox_title);
		pane_inner.setCenter(vbox_buttons);

		// Setting up Title HBox
		hbox_title.getChildren().add(lblTitle);

		// Setting up VBox with MenuButtons
		vbox_buttons.getChildren().addAll(btn_lobby, btn_highscore, btn_settings);

		// Set ID for CSS
		root.setId("root_MainMenu");
		pane_inner.setId("pane_inner");
		hbox_title.setId("hbox_title");
		vbox_buttons.setId("vbox_buttons");
		this.btn_lobby.setId("Button");
		this.lblTitle.setId("lblTitle");
		this.btn_highscore.setId("Button");
		this.btn_settings.setId("Button");

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
		btn_lobby.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		btn_highscore.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		btn_settings.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));

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
}