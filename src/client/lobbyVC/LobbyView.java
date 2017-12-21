package client.lobbyVC;

import client.ServiceLocator;
import client.commonClasses.Translator;
import client.mainMenuVC.MainMenuController;
import client.mainMenuVC.MainMenuView;
import client.modelClasses.MenuModel;
import client.resourceClasses.ListEntry;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import shared.abstractClasses.View;

/**
 * 
 * @author Philipp Lehmann
 *
 */
public class LobbyView extends View<MenuModel> {
	protected Scene scene;
	protected StackPane overlay; // Philip Käppeli
	protected StackPane stackpane;
	protected BorderPane root;
	protected BorderPane pane_lobby;
	protected Pane spacer;
	protected HBox hbox_title;
	protected HBox hbox_buttons;
	protected Label lblTitle;
	protected Button btnBack;
	protected Button btnSetting;
	protected Button btnStart;
	protected Button btnChat;
	protected VBox vbox_chat;
	protected TextField txtf_input;
	protected TextArea txta_chat;
	protected TableView<ListEntry> table;
	final double FILL = 1.0;
	protected DoubleProperty fontSizeBase13;
	protected DoubleProperty fontSizeBase15;
	protected DoubleProperty fontSizeBase20;

	public LobbyView(Stage stage, MenuModel model) {
		super(stage, model);
	}

	@Override
	protected Scene create_GUI() {
		fontSizeBase13 = new SimpleDoubleProperty();
		fontSizeBase15 = new SimpleDoubleProperty();
		fontSizeBase20 = new SimpleDoubleProperty();

		// Getting Translator
		ServiceLocator sl = ServiceLocator.getServiceLocator();
		Translator t = sl.getTranslator();

		stage.setTitle("Dominion");
		
		// Initialize
		root = new BorderPane();
		stackpane = new StackPane();
		scene = new Scene(stackpane);
		pane_lobby = new BorderPane();
		table = new TableView<ListEntry>();
		hbox_title = new HBox();
		hbox_buttons = new HBox();
		lblTitle = new Label();
		btnBack = new Button();
		btnSetting = new Button();
		btnStart = new Button();
		btnChat = new Button();	

		// Philip: Initial dynamic bind
		stackpane.minWidthProperty().bind(stage.widthProperty());
		stackpane.maxWidthProperty().bind(stage.widthProperty());
		stackpane.minHeightProperty().bind(stage.minHeightProperty());
		stackpane.maxHeightProperty().bind(stage.minHeightProperty());
		bindElement(root, stackpane, 1.0, 1.0);
		// overlay
		overlay = new StackPane();
		overlay.setId("overlay");
		overlay.setVisible(false);
		bindElement(overlay, root, 1.0, 1.0);
		// ---

		// bind all elements to their parents
		bindElement(pane_lobby, root, 0.45, 0.6);		
		bindElement(hbox_title, pane_lobby, 0.9, 0.2);		
		bindElement(hbox_buttons, pane_lobby, FILL, 0.1);
		bindElement(lblTitle, hbox_title, FILL, FILL);
		bindElement(btnBack, hbox_buttons, 0.2, 0.73);
		bindElement(btnSetting, hbox_buttons, 0.2, 0.73);
		bindElement(btnStart, hbox_buttons, 0.2, 0.73);
		bindElement(btnChat, hbox_buttons, 0.2, 0.73);
		
		hbox_buttons.setAlignment(Pos.BASELINE_CENTER);
		
		// set up translatable texts
		lblTitle.setText(t.getString("lobby.lbl_title"));		
		btnBack.setText(t.getString("lobby.btn_back"));
		btnSetting.setText(t.getString("lobby.btn_settings"));
		btnStart.setText(t.getString("lobby.btn_start"));
		btnChat.setText(t.getString("lobby.btn_chat"));
		
		btnBack.setDisable(false);
		btnSetting.setDisable(true);		
		btnStart.setDisable(true);		

		// customize tableview
		table.setEditable(false);
		table.setSelectionModel(null);
		table.setPlaceholder(new Label(""));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<ListEntry, String> player = new TableColumn<ListEntry, String>("PLAYER");
		player.setSortable(false);
		player.setCellValueFactory(new PropertyValueFactory<ListEntry, String>("name"));

		TableColumn<ListEntry, String> role = new TableColumn<ListEntry, String>("ROLE");
		role.setSortable(false);
		role.setCellValueFactory(new PropertyValueFactory<ListEntry, String>("parameter"));
		table.getColumns().addAll(player, role);

		// set IDs for CSS
		root.setId("root_Lobby");
		pane_lobby.setId("pane_inner");
		hbox_title.setId("hbox_title");
		btnBack.setId("Button");
		btnSetting.setId("Button");
		btnStart.setId("Button");
		btnChat.setId("Button");
		lblTitle.setId("lblTitle");
		table.setId("tableview");

		// GET CSS FORMATTING
		scene.getStylesheets().add(getClass().getResource("/client/layout/all.css").toExternalForm());
		
		// add content to Panes
		stackpane.getChildren().addAll(root, overlay);
		root.setCenter(pane_lobby);
		root.setRight(vbox_chat);
		pane_lobby.setTop(hbox_title);
		pane_lobby.setCenter(table);
		pane_lobby.setBottom(hbox_buttons);
		hbox_title.getChildren().add(lblTitle);
		hbox_buttons.getChildren().addAll(btnBack, btnSetting, btnStart, btnChat);	

		stage.setScene(scene);
		bindTexts(scene);
		return scene;
	}

	/**
	 * Show a message (game is currently running)
	 * 
	 * @author Philip Käppeli
	 * @param popupType
	 */
	public void showMessageOverlay(String message, boolean returnToMenu) {
		Translator t = ServiceLocator.getServiceLocator().getTranslator();
		overlay.getChildren().clear();
		StackPane sp = new StackPane();
		VBox vBox = new VBox();
		// Message
		Label lblMessage = new Label(message);
		lblMessage.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		// Button ok
		Button bOk = new Button(t.getString("lobby.btn_ok"));
		bOk.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		bOk.setOnAction((e) -> {
			if (returnToMenu) {
				// Return to the main menu
				MenuModel mModel = new MenuModel();
				MainMenuView mmView = new MainMenuView(new Stage(), mModel);
				new MainMenuController(mModel, mmView);
				mmView.start();
				stop();
			} else {
				// Just hide the message
				overlay.setVisible(false);
				overlay.getChildren().clear();
				model.gameRunningMsg.set("");
			}
		});
		vBox.getChildren().addAll(lblMessage, bOk);
		sp.getChildren().addAll(vBox);
		overlay.getChildren().add(sp);
		overlay.setVisible(true);
		bindElement(sp, overlay, 0.21, 0.15); // Message pane
		bindElement(vBox, sp, 0.8, 0.8);
		bindElement(lblMessage, vBox, 1.0, 0.65); // Message
		bindElement(bOk, vBox, 1.0, 0.35); // Button ok

		sp.setId("overlaySP");
		bOk.setId("Button");
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
		table.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase13.asString()));

		fontSizeBase15
				.bind((sc.widthProperty().add(sc.heightProperty())).divide(screenwidth + screenheight).multiply(15));
		btnBack.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		btnSetting.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		btnStart.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		btnChat.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));

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