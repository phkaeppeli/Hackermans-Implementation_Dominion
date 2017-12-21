package client.chatVC;

import client.modelClasses.GameModel;
import client.modelClasses.MenuModel;
import client.soundeffects.Sound;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import shared.abstractClasses.Controller;
import shared.abstractClasses.Model;
import shared.resourceClasses.GameMessage;
import shared.resourceClasses.MessageType;

/**
 * The Controller class for Chat MVC, Creates and handles a Chat conversation between players in Lobby and Game
 * 
 * @author Pascal Moll
 *
 */

public class ChatController extends Controller<Model, ChatView> {
	private static boolean chatOpen = false;
/**
 * Checks if MenuModel or GameModel is used for chat + Sets Buttons on Action
 * 
 * @author Pascal Moll
 * @param model
 * @param view
 */
	public ChatController(Model model, ChatView view) {
		super(model, view);

		// Catch window close event
		view.getStage().setOnHiding((event) -> {
			chatOpen = false;
		});
		
		// Checks if the chat is opened in LOBBY or GAME.
		if (model instanceof MenuModel) {
			chatOpen = true;
			showChatHystoryLobby();
			buildLobbyChat();
			
			//Send Message via enter
			view.txtf_input.setOnKeyPressed((e) -> {
				if (e.getCode().toString() == "ENTER") {
					view.btn_send.fire();
				}
			});
			
			//Button send, sends message if clicked.
			view.btn_send.setOnAction((event) -> {
				Sound.playButton();
				sendLobbyMessage(model);
			});

		} else if (model instanceof GameModel) {
			showChatHystoryGame();
			buildGameChat();

			//Send Message via enter
			view.txtf_input.setOnKeyPressed((e) -> {
				if (e.getCode().toString() == "ENTER") {
					view.btn_send.fire();
				}
			});

			//Button send, sends message if clicked.
			view.btn_send.setOnAction((event) -> {
				Sound.playButton();
				sendGameMessage(model);
			});
		}
	}

	
	/**
	 * adds a Listener to observable list in MENUMODEL in Case chat is opened in Lobby.
	 * 
	 * @author Pascal Moll
	 */
	public void buildLobbyChat() {
		MenuModel mModel = (MenuModel) model;
		mModel.chatMessages.addListener(new ListChangeListener<Object>() {
			@Override
			public void onChanged(Change<?> c) {
				Platform.runLater(() -> {
					buildChat(mModel.chatMessages);
				});
			}
		});
	}

	/**
	 * adds a Listener to observable list in GAMEMODEL in Case chat is opened in Lobby.
	 * @author Pascal Moll
	 */
	public void buildGameChat() {
		// GAME: Gets Observable list and displays it on textarea chat
		GameModel gModel = (GameModel) model;
		gModel.getChatMessages().addListener(new ListChangeListener<Object>() {

			@Override
			public void onChanged(Change<?> arg0) {
				buildChat(gModel.getChatMessages());
			}
		});
	}

	/**
	 * Displays all chatMesages on ChatView textarea if a new Message arrived.
	 * @author Pascal Moll
	 * @param messages
	 */
	public void buildChat(ObservableList<String> messages) {
		view.txta_chat.clear();
		Platform.runLater(() -> {
			for (int i = 0; i < messages.size(); i++) {
				view.txta_chat.setText(view.txta_chat.getText() + "\n" + messages.get(i));
			}
		});
	}

	/**
	 * Creates a Message for sending a Message if Chat is opened in LOBBY
	 * @author Pascal Moll
	 * @param model
	 */
	public void sendLobbyMessage(Model model) {
		MenuModel mModel = (MenuModel) model;
		if (!view.txtf_input.getText().trim().isEmpty()) {
			// Sending Message from User as Gamemessage to server
			String input = mModel.getUsername() + ": " + view.txtf_input.getText().trim();
			view.txtf_input.clear();
			new GameMessage(MessageType.CHATMESSAGE, input).send(mModel.getClient().getSocket());
		}
	}

	/** 
	 * Creates a Message for sending a Message if Chat is opened in GAME
	 * @author Pascal Moll
	 * @param model
	 */
	public void sendGameMessage(Model model) {
		GameModel gModel = (GameModel) model;
		if (!view.txtf_input.getText().trim().isEmpty()) {
			// Sending Message from User as Gamemessage to server
			String input = gModel.getUsername() + ": " + view.txtf_input.getText().trim();
			view.txtf_input.clear();
			new GameMessage(MessageType.CHATMESSAGE, input).send(gModel.getClient().getSocket());
		}
	}

	/**
	 * Displays the chathistory on Chatview textarea if Chat is opened in LOBBY
	 * @author Pascal Moll
	 */
	public void showChatHystoryLobby() {
		MenuModel mModel = (MenuModel) model;
		// Shows Chathistory, if Chatwindow is open
		Platform.runLater(() -> {
			for (int i = 0; i < mModel.chatMessages.size(); i++) {
				view.txta_chat.setText(view.txta_chat.getText() + "\n" + mModel.chatMessages.get(i));
			}
		});
	}

	/**
	 * Displays the chathistory on Chatview textarea if Chat is opened in GAME
	 * @author Pascal Moll
	 */
	public void showChatHystoryGame() {
		GameModel gModel = (GameModel) model;

		// Shows Chathistory, if Chatwindow is open without turnaround method
		Platform.runLater(() -> {
			for (int i = 0; i < gModel.getChatMessages().size(); i++) {
				view.txta_chat.setText(view.txta_chat.getText() + "\n" + gModel.getChatMessages().get(i));
			}
		});
	}
	
	public static boolean getChatOpen(){
		return chatOpen;
	}
	
	public static void setChatOpen(Boolean chatOpen){
		ChatController.chatOpen = chatOpen;
	}
}
