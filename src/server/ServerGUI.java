package server;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * A simple GUI for the server
 * 
 * @author Mario Allemann
 *
 */
public class ServerGUI {
	private Stage stage;
	private final int WINDOWSIZE = 400;
	// ROOT
	private BorderPane root;

	// TOP Shows IP and port
	private Label lblIP;
	private Label lblPort;

	// CENTER Shows the messages between client and server
	private TextFlow messages;

	public ServerGUI(String IP, int port) {
		// Stage
		stage = new Stage();
		stage.setTitle("Dominion Server");
		stage.setMinWidth(WINDOWSIZE);
		stage.setMinHeight(WINDOWSIZE);

		// ROOT
		root = new BorderPane();
		root.setMinWidth(stage.getMinWidth());
		root.setMinHeight(stage.getMinHeight());
		root.setMaxWidth(stage.getMaxWidth());
		root.setMaxHeight(stage.getMaxHeight());

		// TOP
		HBox top = new HBox();
		lblIP = new Label("IP: " + IP);
		lblPort = new Label("Port: " + Integer.toString(port));
		top.setSpacing(10);
		top.getChildren().addAll(lblIP, lblPort);

		root.setTop(top);

		// CENTER
		messages = new TextFlow();
		ScrollPane center = new ScrollPane();
		center.setContent(messages);
		logMessage("Server initialized", "INIT", "");
		root.setCenter(center);

		// Scene
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Logs a message to the server GUI
	 * @param message The parameters of the message
	 * @param type The message type defined by the MessageType enum
	 * @param clientIP The IP of the respective client
	 */
	public void logMessage(String message, String type, String clientIP) {
		String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
		Text text = new Text(timestamp + " " + type + " " + clientIP + " " + message + "\n");
		//Send messages are blue, receive messages green
		if (type.equals("Send: ")) {
			text.setFill(Color.BLUE);
		} else {
			text.setFill(Color.GREEN);
		}
		messages.getChildren().add(text);
	}
}
