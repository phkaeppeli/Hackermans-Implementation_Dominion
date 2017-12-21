package client.chatVC;

import client.ServiceLocator;
import client.commonClasses.Translator;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import shared.abstractClasses.Model;
import shared.abstractClasses.View;
/**
 * The View class for the Chat MVC. Has A Textarea to display chat, TextField to write and Button to send.
 *  
 * @author Pascal Moll
 *
 */
public class ChatView extends View<Model> {
	
	private BorderPane root;
	private HBox hbox_input;
	protected TextArea txta_chat;
	protected TextField txtf_input;
	protected Button btn_send;
	private final double FILL = 1.0;
	private final double STAGE_HEIGHT = 600;
	private final double STAGE_WIDTH = 450;
	
	/**
	 * CONSTRUCTOR: default
	 * @param stage
	 * @param model
	 */
	public ChatView(Stage stage, Model model) {
		super(stage, model);
	}
	
	/**
	 *Create and show the Chat GUI, includes elements: Area other players,
	 *Included Elements: TextArea for Chathystory, TextField for Writing new Chats, Send Button
	 * 
	 * @author Pascal Moll
	 */
	@Override
	protected Scene create_GUI() {
		
		//Getting Translator
		ServiceLocator sl = ServiceLocator.getServiceLocator();
		Translator t = sl.getTranslator();
		
		stage.setTitle("Dominion");
		
		this.root = new BorderPane();
		
		this.hbox_input = new HBox();
		bindElement(hbox_input, root, FILL, 0.07);
		
		this.txta_chat = new TextArea();
		bindElement(txta_chat, root, FILL, 0.93);
		txta_chat.setEditable(false);
		
		this.txtf_input = new TextField();
		bindElement(txtf_input, hbox_input, 0.7, FILL);
		
		this.btn_send = new Button(t.getString("chat.btn_send"));
		bindElement(btn_send, hbox_input, 0.3, FILL);
		
		this.root.setCenter(txta_chat);
		this.root.setBottom(hbox_input);
		this.hbox_input.getChildren().addAll(txtf_input,btn_send);
		
		// set IDs for CSS
		txta_chat.setId("txta_chat");
		txtf_input.setId("txtf_chat");
		btn_send.setId("Button");
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/client/layout/all.css").toExternalForm());		
		stage.setScene(scene);
		stage.setHeight(this.STAGE_HEIGHT);
		stage.setWidth(this.STAGE_WIDTH);
		this.txtf_input.requestFocus();
		
		return scene;
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
