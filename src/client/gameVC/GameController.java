package client.gameVC;

import java.util.ArrayList;

import client.ServiceLocator;
import client.chatVC.ChatController;
import client.chatVC.ChatView;
import client.commonClasses.Translator;
import client.modelClasses.GameModel;
import client.resourceClasses.FieldFX;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.stage.Stage;
import server.resourceClasses.Phase;
import shared.BFCardStack;
import shared.abstractClasses.Controller;
import shared.resourceClasses.StackType;

/**
 * Controller class for the game mvc. Manages all event handlers for the gui
 * elements as well as change listeners for the observable lists/values in the
 * game model.
 * 
 * @author Philip Käppeli
 *
 */
public class GameController extends Controller<GameModel, GameView> {

	public static ChatView chView;

	/**
	 * CONSTRUCTOR: Adds EventHandlers to buttons and observable lists/values
	 * 
	 * @author Philip Käppeli
	 */
	public GameController(GameModel model, GameView view) {
		super(model, view);
		// GENERAL: Window close event
		view.getStage().setOnHiding((event) -> {
			model.disconnectPlayer();
			// Pascall Moll: Stop chat
			Platform.runLater(() -> {
				if (ChatController.getChatOpen()) {
					chView.stop();
					ChatController.setChatOpen(false);
				}
			});
		});
		// CHAT (Pascal Moll): Open Chat by pressing ChatButton
		view.bChat.setOnAction((event) -> {
			if (!ChatController.getChatOpen()) {
				chView = new ChatView(new Stage(), model);
				new ChatController(model, chView);
				chView.start();
				ChatController.setChatOpen(true);
			}
		});
		// OTHER PLAYERS: played changed
		model.getOtherPlayed().addListener(new ListChangeListener<Object>() {
			@Override
			public void onChanged(Change<?> c) {
				Platform.runLater(() -> {
					view.buildOtherAreas();

					// Add event handlers
					for (FieldFX f : view.otherPlayeds) {
						// Add event listeners to other played stack
						if (!f.name.equals("empty")) {
							// Event: MouseEntered/Left
							f.setOnMouseEntered((e) -> {
								view.displayHoverCard(f.name);
							});
							f.setOnMouseExited((e2) -> {
								view.hideHoverCard();
							});
						} else {
							// Event: MouseEntered/Left
							f.setOnMouseEntered((e) -> {
								// do nothing
							});
							f.setOnMouseExited((e2) -> {
								// do nothing
							});
						}
					}
					for (FieldFX f : view.otherDiscards) {
						// Add event listeners to other discard stack
						if (!f.name.equals("empty")) {
							// Event: MouseEntered/Left
							f.setOnMouseEntered((e) -> {
								view.displayHoverCard(f.name);
							});
							f.setOnMouseExited((e2) -> {
								view.hideHoverCard();
							});
						} else {
							// Event: MouseEntered/Left
							f.setOnMouseEntered((e) -> {
								// do nothing
							});
							f.setOnMouseExited((e2) -> {
								// do nothing
							});
						}
					}
				});
			}
		});
		// OTHER PLAYERS: discard changed
		model.getOtherDiscard().addListener(new ListChangeListener<Object>() {
			@Override
			public void onChanged(Change<?> c) {
				Platform.runLater(() -> {
					view.buildOtherAreas();

					// Add event handlers
					// Add event handlers
					for (FieldFX f : view.otherPlayeds) {
						// Add event listeners to other played stack
						if (!f.name.equals("empty")) {
							// Event: MouseEntered/Left
							f.setOnMouseEntered((e) -> {
								view.displayHoverCard(f.name);
							});
							f.setOnMouseExited((e2) -> {
								view.hideHoverCard();
							});
						} else {
							// Event: MouseEntered/Left
							f.setOnMouseEntered((e) -> {
								// do nothing
							});
							f.setOnMouseExited((e2) -> {
								// do nothing
							});
						}
					}
					for (FieldFX f : view.otherDiscards) {
						// Add event listeners to other discard stack
						if (!f.name.equals("empty")) {
							// Event: MouseEntered/Left
							f.setOnMouseEntered((e) -> {
								view.displayHoverCard(f.name);
							});
							f.setOnMouseExited((e2) -> {
								view.hideHoverCard();
							});
						} else {
							// Event: MouseEntered/Left
							f.setOnMouseEntered((e) -> {
								// do nothing
							});
							f.setOnMouseExited((e2) -> {
								// do nothing
							});
						}
					}
				});
			}
		});
		// BATTLEFIELD: bf stacklist init
		model.getBFStacks().addListener(new ListChangeListener<Object>() {
			@Override
			public void onChanged(Change<?> c) {
				Platform.runLater(() -> {
					// Clear stacks
					model.getMoneyCards().clear();
					model.getPointCards().clear();
					model.getSelActionCards().clear();

					// Set battlefield stacks
					for (BFCardStack bfc : model.getBFStacks()) {
						if (bfc.getType() == StackType.MONEY) {
							model.getMoneyCards().add(bfc.getName());
						} else if (bfc.getType() == StackType.POINT) {
							model.getPointCards().add(bfc.getName());
						} else {
							model.getSelActionCards().add(bfc.getName());
						}
					}

					view.buildBattleField();
					setBFFieldListeners();
					updateGUI();
				});
			}
		});
		// BATTLEFIELD: bf stack change listener
		model.getBFchange().addListener(new ListChangeListener<Object>() {
			@Override
			public void onChanged(Change<?> c) {
				Platform.runLater(() -> {
					model.setBFStackSize(model.getBFchange().get(0), model.getBFchange().get(1));
					view.buildBattleField();
					setBFFieldListeners();
					updateGUI();
				});
			}
		});
		setPlayerListeners();
		setPhaseListeners();
	}

	/**
	 * PLAYER: Listeners for the player gui elements and observable lists/values
	 * 
	 * @author Philip Käppeli
	 */
	private void setPlayerListeners() {
		// Drawstack size changed
		model.getDrawSize().addListener((c) -> {
			Platform.runLater(() -> {
				if (model.getCurrentPhase() != null) {
					// Simply update the GUI to match the new phase
					view.drawField.setStacksize(model.getDrawSize().get());
					updateGUI();
				}
			});
		});
		// Buy/play denied message received
		model.getDenyMessage().addListener((c) -> {
			Platform.runLater(() -> {
				if (!model.getDenyMessage().get().equals("")) {
					view.showMessageOverlay(model.getDenyMessage().get(), false);
				}
			});
		});
		// Hand list changed
		model.getPlayerHand().addListener(new ListChangeListener<Object>() {
			@Override
			public void onChanged(Change<?> c) {
				Platform.runLater(() -> {
					if (model.getPlayerHand() != null) {
						// Update player hand
						view.buildPlayerHand(model.getPlayerHand().size());
						view.showPlayerHand(model.getCurrentHandPage());
						updateGUI();

						// Add event listeners to new player hand
						for (ArrayList<FieldFX> list : view.playerHandFull) {
							for (FieldFX f : list) {
								// Event: MouseEntered/Left
								f.setOnMouseEntered((e) -> {
									view.displayHoverCard(f.name);
								});
								f.setOnMouseExited((e2) -> {
									view.hideHoverCard();
								});
								// Event: Clicked
								f.setOnMouseClicked((event) -> {
									if (!f.getActionDisabled()) {
										int index = model.getCurrentHandPage() * model.getMAX_PAGESIZE();
										index += view.playerHandFull.get(model.getCurrentHandPage()).indexOf(f);
										model.playCard(index);
										updateGUI();
									}
								});
							}
						}
					}
				});
			}
		});
		// Played list changed
		model.getPlayerPlayed().addListener(new ListChangeListener<Object>() {
			@Override
			public void onChanged(Change<?> c) {
				Platform.runLater(() -> {
					if (model.getPlayerPlayed() != null) {
						// Update player hand
						view.buildPlayerPlayed(model.getPlayerPlayed().size());
						view.showPlayerPlayed(model.getCurrentPlayedPage());
						updateGUI();
					}
					// Add event listeners to new player played
					for (ArrayList<FieldFX> list : view.playerPlayedFull) {
						for (FieldFX f : list) {
							// Event: MouseEntered/Left
							f.setOnMouseEntered((e) -> {
								view.displayHoverCard(f.name);
							});
							f.setOnMouseExited((e2) -> {
								view.hideHoverCard();
							});
						}
					}
				});
			}
		});
		// Discard card name and/or stack size changed
		model.getDiscardStack().addListener(new ListChangeListener<Object>() {
			@Override
			public void onChanged(Change<?> c) {
				Platform.runLater(() -> {
					String cardName = model.getDiscardStack().get(0);
					view.discardField.setName(cardName);
					view.discardField.setStacksize(model.getDiscardStack().get(1));

					// Add event listeners to discard stack
					if (!cardName.equals("empty")) {
						// Event: MouseEntered/Left
						view.discardField.setOnMouseEntered((e) -> {
							view.displayHoverCard(cardName);
						});
						view.discardField.setOnMouseExited((e2) -> {
							view.hideHoverCard();
						});
					} else {
						// Event: MouseEntered/Left
						view.discardField.setOnMouseEntered((e) -> {
							// do nothing
						});
						view.discardField.setOnMouseExited((e2) -> {
							// do nothing
						});
					}
				});
			}
		});
		// Buttons for swapping player hand page
		view.btnPlayerHandRight.setOnAction((event) -> {
			model.setCurrentHandPage(model.getCurrentHandPage() + 1);
			view.showPlayerHand(model.getCurrentHandPage());
			updateGUI();
		});
		view.btnPlayerHandLeft.setOnAction((event) -> {
			model.setCurrentHandPage(model.getCurrentHandPage() - 1);
			view.showPlayerHand(model.getCurrentHandPage());
			updateGUI();
		});
		// Buttons for swapping player played page
		view.btnPlayerPlayedRight.setOnAction((event) -> {
			model.setCurrentPlayedPage(model.getCurrentPlayedPage() + 1);
			view.showPlayerPlayed(model.getCurrentPlayedPage());
			updateGUI();
		});
		view.btnPlayerPlayedLeft.setOnAction((event) -> {
			model.setCurrentPlayedPage(model.getCurrentPlayedPage() - 1);
			view.showPlayerPlayed(model.getCurrentPlayedPage());
			updateGUI();
		});
	}

	/**
	 * PHASE: Listeners for the phase gui elements and observable lists/values
	 * 
	 * @author Philip Käppeli
	 */
	private void setPhaseListeners() {
		// Phase change listener
		model.getCurrentPhase().addListener((c) -> {
			Platform.runLater(() -> {
				Translator t = ServiceLocator.getServiceLocator().getTranslator();
				if (model.getCurrentPhase().get().equals("end")) {
					view.showEndgameOverlay(model.getScoreList());
				} else if (model.getCurrentPhase().get().equals("cancel")) {
					view.showMessageOverlay(t.getString("game.gameCancelled"), true);
				} else {
					// Simply update the GUI to match the new phase
					updateGUI();
				}
			});
		});
		// Next phase button
		view.bNextPhase.setOnAction((event) -> {
			model.nextPhase();
		});
		// End turn button
		view.bEndTurn.setOnAction((event) -> {
			model.nextPhase();
		});
	}

	/**
	 * BATTLEFIELD: Set listeners for the battlefield card stacks seperately as they
	 * need to be re-set after changes happened to the battlefield
	 * 
	 * @author Philip Käppeli
	 */
	public void setBFFieldListeners() {
		// Add money card event handlers
		for (FieldFX f : view.moneyFields) {
			// EVENT: MouseEntered/Left
			f.setOnMouseEntered((e) -> {
				view.displayHoverCard(f.name);
			});
			f.setOnMouseExited((e2) -> {
				view.hideHoverCard();
			});
			// EVENT: Clicked
			f.setOnMouseClicked((event) -> {
				if (!f.getActionDisabled()) {
					model.buyCard(f.name);
					updateGUI();
				}
			});
		}

		// Add point card event handlers
		for (FieldFX f : view.pointFields) {
			// EVENT: MouseEntered/Left
			f.setOnMouseEntered((e) -> {
				view.displayHoverCard(f.name);
			});
			f.setOnMouseExited((e2) -> {
				view.hideHoverCard();
			});
			// EVENT: Clicked
			f.setOnMouseClicked((event) -> {
				if (!f.getActionDisabled()) {
					model.buyCard(f.name);
					updateGUI();
				}
			});
		}

		// Add action card event handlers
		for (FieldFX f : view.actionFields) {
			// EVENT: MouseEntered/Left
			f.setOnMouseEntered((e) -> {
				view.displayHoverCard(f.name);
			});
			f.setOnMouseExited((e2) -> {
				view.hideHoverCard();
			});
			// EVENT: Clicked
			f.setOnMouseClicked((event) -> {
				if (!f.getActionDisabled()) {
					model.buyCard(f.name);
					updateGUI();
				}
			});
		}
	}

	/**
	 * Update the local locking mechanism and the currentPhase TextField
	 * 
	 * @author Philip Käppeli
	 */
	public void updateGUI() {
		// Update battlefield
		for (FieldFX f : view.actionFields) {
			f.setLocked(!model.getCurrentPhase().get().equalsIgnoreCase(Phase.BUY.toString()));
		}
		for (FieldFX f : view.moneyFields) {
			f.setLocked(!model.getCurrentPhase().get().equalsIgnoreCase(Phase.BUY.toString()));
		}
		for (FieldFX f : view.pointFields) {
			f.setLocked(!model.getCurrentPhase().get().equalsIgnoreCase(Phase.BUY.toString()));
		}
		// Update player hand
		for (ArrayList<FieldFX> list : view.playerHandFull) {
			for (FieldFX f : list) {
				f.setLocked(!((model.getCurrentPhase().get().equalsIgnoreCase(Phase.ACTION.toString())
						&& model.getSelActionCards().contains(f.name)) // action phase -> enable only action cards
						|| (model.getCurrentPhase().get().equalsIgnoreCase(Phase.BUY.toString())
								&& model.getMoneyCards().contains(f.name)))); // buy phase -> enable only money cards
			}
		}
		// Update player hand
		for (ArrayList<FieldFX> list : view.playerPlayedFull) {
			for (FieldFX f : list) {
				f.setLocked(true);
			}
		}
		// Update buttons playerhand
		view.btnPlayerHandRight.setVisible(model.getCurrentHandPage() < model.getNumHandPages() - 1);
		view.btnPlayerHandLeft.setVisible(!(model.getCurrentHandPage() == 0));
		// Update buttons playerplayed
		view.btnPlayerPlayedRight.setVisible(model.getCurrentPlayedPage() < model.getNumPlayedPages() - 1);
		view.btnPlayerPlayedLeft.setVisible(!(model.getCurrentPlayedPage() == 0));
		// Update phase buttons
		view.bNextPhase.setDisable(model.getCurrentPhase().get().equalsIgnoreCase(Phase.BUY.toString())
				|| model.getCurrentPhase().get().equalsIgnoreCase(Phase.WAITING.toString()));
		view.bEndTurn.setDisable(!model.getCurrentPhase().get().equalsIgnoreCase(Phase.BUY.toString()));
		// Update phase label
		view.tfPhase.setText(model.getCurrentPhaseStr());
	}
}
