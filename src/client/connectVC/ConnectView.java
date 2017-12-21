package client.connectVC;

import client.modelClasses.MenuModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import shared.abstractClasses.View;

/**
 * View for Connect MVC, Has Textfields to enter Port and IP Number and a Button
 * for confirming.
 * 
 * @author Pascal Moll
 *
 */
public class ConnectView extends View<MenuModel> {

	// Defining all Instance Variables
	protected BorderPane root;

	protected Label lblTitle;
	protected Label lbl_lp;
	protected Label lbl_port;
	protected Button btn_connect;
	protected Label lbl_status;
	protected TextField txtf_lp;
	protected TextField txtf_port;
	protected final double FILL = 1.0;
	protected DoubleProperty fontSizeBase13;
	protected DoubleProperty fontSizeBase15;
	protected DoubleProperty fontSizeBase20;

	/**
	 * Constructor: Default
	 * @param stage
	 * @param model
	 */
	public ConnectView(Stage stage, MenuModel model) {
		super(stage, model);
	}

	/**
	 * Creates and Shows the Connect GUI. Conatins Elements: TextFields to type in the Portnumber and IP-Address
	 * Button for Connecting
	 * 
	 * @author Pascal Moll
	 */
	@Override
	protected Scene create_GUI() {
		fontSizeBase13 = new SimpleDoubleProperty();
		fontSizeBase15 = new SimpleDoubleProperty();
		fontSizeBase20 = new SimpleDoubleProperty();

		// Setting up Panes
		root = new BorderPane();
		BorderPane pane_inner = new BorderPane();
		bindElement(pane_inner, root, 0.3, 0.4);
		HBox hbox_title = new HBox();
		bindElement(hbox_title, pane_inner, 0.9, 0.2);

		HBox hbox_buttons = new HBox();
		bindElement(hbox_buttons, pane_inner, FILL, 0.1);
		GridPane inside = new GridPane();
		bindElement(inside, pane_inner, FILL, 0.7);

		// Setting Up Labels, Buttons, TextField
		this.lblTitle = new Label("Connection");
		bindElement(lblTitle, hbox_title, FILL, FILL);
		this.lbl_lp = new Label("IP: ");
		bindElement(lbl_lp, inside, 0.2, 0.2);
		this.lbl_port = new Label("Port: ");
		bindElement(lbl_port, inside, 0.2, 0.2);

		this.btn_connect = new Button("OK");
		bindElement(btn_connect, hbox_buttons, 0.2, 1.1);
		this.lbl_status = new Label("");
		bindElement(lbl_status, hbox_buttons, 0.2, 1.1);

		this.txtf_port = new TextField();

		bindElement(txtf_port, inside, 0.5, 0.1);
		this.txtf_lp = new TextField();
		bindElement(txtf_lp, inside, 0.5, 0.1);

		// TODO Delete these default values
		this.txtf_lp.setText("127.0.0.1");
		this.txtf_port.setText("1116");

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
		inside.add(this.lbl_lp, 0, 2);
		inside.add(txtf_lp, 1, 2);
		inside.add(this.lbl_port, 0, 4);
		inside.add(this.txtf_port, 1, 4);

		// Setting up Title Hbox
		hbox_title.getChildren().add(lblTitle);

		// Setting up bottom - buttons Hbox
		hbox_buttons.getChildren().addAll(btn_connect, lbl_status);

		// Set ID for CSS
		root.setId("root_Connect");
		pane_inner.setId("pane_inner");
		hbox_title.setId("hbox_title");
		inside.setId("gridpane_inside");
		this.btn_connect.setId("Button");
		this.lblTitle.setId("lblTitle");
		this.lbl_port.setId("lbl_username");
		this.txtf_port.setId("txtf_username");
		this.lbl_lp.setId("lbl_username");
		this.txtf_lp.setId("txtf_username");

		// Setting up primaryStage
		stage.setTitle("Dominion");
		stage.setMaximized(true);

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
	 */
	private void bindTexts(Scene sc) {
		// Source: stackoverflow.com/questions/23705654
		// Source: docs.oracle.com/javafx/2/api/javafx/stage/Screen.html
		double screenwidth = Screen.getPrimary().getVisualBounds().getWidth();
		double screenheight = Screen.getPrimary().getVisualBounds().getHeight();

		fontSizeBase13
				.bind((sc.widthProperty().add(sc.heightProperty())).divide(screenwidth + screenheight).multiply(13));
		txtf_lp.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase13.asString()));
		txtf_port.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase13.asString()));

		fontSizeBase15
				.bind((sc.widthProperty().add(sc.heightProperty())).divide(screenwidth + screenheight).multiply(15));
		lbl_lp.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		lbl_port.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		btn_connect.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		lbl_status.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));

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
	protected void bindElement(Region child, Region parent, double widthMulti, double heightMulti) {
		child.minWidthProperty().bind(parent.widthProperty().multiply(widthMulti));
		child.maxWidthProperty().bind(parent.widthProperty().multiply(widthMulti));
		child.minHeightProperty().bind(parent.heightProperty().multiply(heightMulti));
		child.maxHeightProperty().bind(parent.heightProperty().multiply(heightMulti));
	}
}
