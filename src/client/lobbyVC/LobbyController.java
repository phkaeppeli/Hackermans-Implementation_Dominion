package client.lobbyVC;

import client.cardSelVC.CardSelectionController;
import client.cardSelVC.CardSelectionView;
import client.chatVC.ChatController;
import client.chatVC.ChatView;
import client.gameVC.GameController;
import client.gameVC.GameView;
import client.mainMenuVC.MainMenuController;
import client.mainMenuVC.MainMenuView;
import client.modelClasses.GameModel;
import client.modelClasses.MenuModel;
import client.resourceClasses.ClientConnector;
import client.soundeffects.Sound;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.stage.Stage;
import shared.abstractClasses.Controller;
import shared.resourceClasses.MessageType;

/**
 * 
 * @author Philip Käppeli
 *
 */

public class LobbyController extends Controller<MenuModel, LobbyView> {
	private MainMenuView mmView;
	public static ChatView chView;
	private CardSelectionView csView;

	public LobbyController(MenuModel model, LobbyView view) {
		super(model, view);

		// Catch window close event
		view.getStage().setOnHiding((event) -> {
			model.disconnectClient();
			// Pascal: Close Chat if a view is open
			if (ChatController.getChatOpen()) {
				chView.stop();
				ChatController.setChatOpen(false);
			}
		});

		// Change listener for the lobby list
		model.lobbyList.addListener(new ListChangeListener<Object>() {
			@Override
			public void onChanged(Change<?> c) {
				Platform.runLater(() -> {
					if (model.lobbyList != null) {
						view.table.setItems(model.lobbyList);
						if (model.lobbyList.size() < 2) {
							model.isLobbyReady.set(false);
						} else {
							model.isLobbyReady.set(true);
						}
					}
				});
			}
		});

		// Change listener to enable the settings for the leader only
		model.isLobbyLeader.addListener((change) -> {
			if (model.isLobbyLeader.get()) {
				Platform.runLater(() -> {
					view.btnSetting.setDisable(false);
				});
			}
		});

		// Change listener to enable the start button for the leader
		model.isLobbyReady.addListener((change) -> {
			Platform.runLater(() -> {
				if (model.isLobbyReady.get() && model.isLobbyLeader.get()) {
					view.btnStart.setDisable(false);
				} else {
					view.btnStart.setDisable(true);
				}
			});
		});

		// Change listener to wait for game start
		model.startGame.addListener((change) -> {
			Platform.runLater(() -> {
				// Start the game
				GameModel gModel = new GameModel();
				GameView gView = new GameView(new Stage(), gModel);		
				gView.getStage().setHeight(view.getStage().getHeight());
				gView.getStage().setWidth(view.getStage().getWidth());
				gView.getStage().setX(view.getStage().getX());
				gView.getStage().setY(view.getStage().getY());
				GameController gCon = new GameController(gModel, gView);
				gCon.updateGUI();
				gModel.setPort(model.getPort());
				gModel.setHostname(model.getHostname());
				gModel.setUsername(model.getUsername());
				ClientConnector client = new ClientConnector();
				client.createConnection(MessageType.GAMECONNECTION, gModel);
				gModel.setClient(client);
				gView.start();

				// Pascal: Close Chat if a view is open
				if (ChatController.getChatOpen()) {
					chView.stop();
					ChatController.setChatOpen(false);
				}
				view.stop();
			});
		});

		// Add changelistener to lobby message stringproperty
		model.gameRunningMsg.addListener((c) -> {
			Platform.runLater(() -> {
				if (!model.gameRunningMsg.get().equals("")) {
					view.showMessageOverlay(model.gameRunningMsg.get(), false);
					model.gameRunningMsg.set("");
				}
			});
		});

		// Add changelistener to lobby message stringproperty
		model.lobbyFullMsg.addListener((c) -> {
			Platform.runLater(() -> {
				if (!model.lobbyFullMsg.get().equals("")) {
					view.showMessageOverlay(model.lobbyFullMsg.get(), true);
					model.lobbyFullMsg.set("");
				}
			});
		});

		// Add ActionHandler for back button
		view.btnBack.setOnAction((event) -> {
			Sound.playButton();
			mmView = new MainMenuView(new Stage(), model);
			mmView.getStage().setHeight(view.getStage().getHeight());
			mmView.getStage().setWidth(view.getStage().getWidth());
			mmView.getStage().setX(view.getStage().getX());
			mmView.getStage().setY(view.getStage().getY());
			new MainMenuController(model, mmView);
			mmView.start();

			// Pascal: Close Chat if a view is open
			if (ChatController.getChatOpen()) {
				chView.stop();
				ChatController.setChatOpen(false);
			}
			view.stop();
		});

		// Add ActionHandler for Chat button
		view.btnChat.setOnAction((event) -> {
			Sound.playButton();
			if (!ChatController.getChatOpen()) {
				chView = new ChatView(new Stage(), model);
				new ChatController(model, chView);
				chView.start();
				ChatController.setChatOpen(true);
			}
		});

		view.btnSetting.setOnAction((event) -> {
			Sound.playButton();
			csView = new CardSelectionView(new Stage(), model);
			new CardSelectionController(model, csView);
			csView.start();
		});

		view.btnStart.setOnAction((event) -> {
			model.startGame();

			// Pascal: Close Chat if a view is open
			if (ChatController.getChatOpen()) {
				chView.stop();
				ChatController.setChatOpen(false);
			}
		});
	}

}
