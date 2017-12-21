package client.loginVC;

import client.ClientMain;
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
 * View for LogIn MVC. Has a TextField for entering Username and a Button to
 * Confirm.
 * 
 * @author Pascal Moll
 *
 */

public class LogInView extends View<MenuModel> {

	// Defining all Instance Variables
	protected Label lblTitle;
	protected Label lbl_username;
	protected Button btn_login;
	protected TextField txtf_username;
	protected ClientMain main;
	protected final double FILL = 1.0;
	protected DoubleProperty fontSizeBase13;
	protected DoubleProperty fontSizeBase15;
	protected DoubleProperty fontSizeBase20;

	/**
	 * Constructor: Checks Local Configuration if Sound has to be palyed or not.
	 * 
	 * @author Pascal Moll
	 * @param stage
	 * @param model
	 */
	public LogInView(Stage stage, MenuModel model) {
		super(stage, model);

		// Check if Music/SoundFX has to be played or not.
		if (ServiceLocator.getServiceLocator().getConfiguration().getOption("Music").equals("on")) {
			Sound.playBackgroundSound = true;
		} else {
			Sound.playBackgroundSound = false;
		}
		if (ServiceLocator.getServiceLocator().getConfiguration().getOption("SoundFX").equals("on")) {
			Sound.playButtonSound = true;
		} else {
			Sound.playButtonSound = false;
		}
		Sound.playSong();
	}
	/**
	 * Create and show the Login GUI: Contains TextField for typing in Username. Button for Confirmation
	 * 
	 * @author Pascal Moll
	 */
	@Override
	protected Scene create_GUI() {
		fontSizeBase13 = new SimpleDoubleProperty();
		fontSizeBase15 = new SimpleDoubleProperty();
		fontSizeBase20 = new SimpleDoubleProperty();

		// Getting Translator
		ServiceLocator sl = ServiceLocator.getServiceLocator();
		Translator t = sl.getTranslator();

		// Setting up Panes
		BorderPane root = new BorderPane();
		BorderPane pane_inner = new BorderPane();
		bindElement(pane_inner, root, 0.3, 0.4);
		HBox hbox_title = new HBox();
		bindElement(hbox_title, pane_inner, 0.9, 0.2);

		HBox hbox_buttons = new HBox();
		bindElement(hbox_buttons, pane_inner, FILL, 0.1);
		GridPane inside = new GridPane();
		bindElement(inside, pane_inner, FILL, 0.7);

		// Setting Up Labels, Buttons, TextField
		this.lblTitle = new Label(t.getString("login.lbl_reg"));
		bindElement(lblTitle, hbox_title, FILL, FILL);
		this.lbl_username = new Label(t.getString("login.lbl_username"));
		bindElement(lbl_username, inside, FILL, 0.2);
		this.btn_login = new Button(t.getString("login.btn_login"));
		bindElement(btn_login, hbox_buttons, 0.2, 1.1);
		this.txtf_username = new TextField();
		bindElement(txtf_username, inside, 0.5, 0.1);

		// Setting up Borderpane root for dynamic Handling
		root.setCenter(pane_inner);
		root.minWidthProperty().bind(stage.widthProperty());
		root.maxWidthProperty().bind(stage.widthProperty());
		root.minHeightProperty().bind(stage.minHeightProperty());
		root.maxHeightProperty().bind(stage.minHeightProperty());

		// Setting pane inner with all panes
		// pane_inner.setTop(hbox_title);
		pane_inner.setTop(hbox_title);
		pane_inner.setCenter(inside);
		pane_inner.setBottom(hbox_buttons);

		// Setting up Gridpane
		inside.add(this.lbl_username, 0, 2);
		inside.add(this.txtf_username, 0, 4);

		// Setting up Title Hbox
		hbox_title.getChildren().add(lblTitle);

		// Setting up bottom - buttons Hbox
		hbox_buttons.getChildren().addAll(btn_login);

		// Set ID for CSS
		root.setId("root_Login");
		pane_inner.setId("pane_inner");
		hbox_title.setId("hbox_title");
		inside.setId("gridpane_inside");
		this.btn_login.setId("Button");
		this.lblTitle.setId("lblTitle");
		this.lbl_username.setId("lbl_username");
		this.txtf_username.setId("txtf_username");

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

		fontSizeBase13
				.bind((sc.widthProperty().add(sc.heightProperty())).divide(screenwidth + screenheight).multiply(13));
		txtf_username.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase13.asString()));

		fontSizeBase15
				.bind((sc.widthProperty().add(sc.heightProperty())).divide(screenwidth + screenheight).multiply(15));
		lbl_username.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		btn_login.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));

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
