package client.gameVC;

import java.util.ArrayList;

import client.ServiceLocator;
import client.commonClasses.Translator;
import client.mainMenuVC.MainMenuController;
import client.mainMenuVC.MainMenuView;
import client.modelClasses.GameModel;
import client.modelClasses.MenuModel;
import client.resourceClasses.FieldFX;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import shared.abstractClasses.View;

/**
 * View class of the game mvc. Builds the area for other players, the battle
 * field, the player's own area as well as the phase/button area.
 * 
 * @author Philip Käppeli
 *
 */
public class GameView extends View<GameModel> {
	// GENERAL
	protected final int MAX_ACTIONROWS = 5;
	protected final double FILL = 1.0;
	protected DoubleProperty fontSizeBase13;
	protected DoubleProperty fontSizeBase15;
	protected DoubleProperty fontSizeBase20;
	// OVERLAY
	protected StackPane overlay;
	// OTHERS AREA
	protected HBox hbOthers;
	protected ArrayList<FieldFX> otherPlayeds;
	protected ArrayList<FieldFX> otherDiscards;
	// BATTLEFIELD AREA
	protected ArrayList<FieldFX> moneyFields;
	protected ArrayList<FieldFX> pointFields;
	protected ArrayList<FieldFX> actionFields;
	protected VBox vbMoneyStacks;
	protected VBox vbPointStacks;
	protected GridPane gpAction;
	protected StackPane spHoverCard;
	protected FieldFX hoverCard;
	// PLAYER AREA
	protected FieldFX drawField;
	protected ArrayList<ArrayList<FieldFX>> playerHandFull;
	protected HBox hbHand;
	protected Button btnPlayerHandLeft;
	protected Button btnPlayerHandRight;
	protected ArrayList<ArrayList<FieldFX>> playerPlayedFull;
	protected HBox hbPlayed;
	protected Button btnPlayerPlayedLeft;
	protected Button btnPlayerPlayedRight;
	protected FieldFX discardField;
	// PHASE AREA
	protected TextField tfPhase;
	protected Button bNextPhase;
	protected Button bEndTurn;
	protected Button bChat;

	/**
	 * CONSTRUCTOR: default
	 * 
	 * @param stage
	 * @param model
	 */
	public GameView(Stage stage, GameModel model) {
		super(stage, model);
	}

	/**
	 * Create and show the game GUI, includes elements: Area other players,
	 * battlefield, area player, area phase
	 * 
	 * @author Philip Käppeli
	 */
	@Override
	protected Scene create_GUI() {
		fontSizeBase13 = new SimpleDoubleProperty();
		fontSizeBase15 = new SimpleDoubleProperty();
		fontSizeBase20 = new SimpleDoubleProperty();

		otherPlayeds = new ArrayList<FieldFX>();
		otherDiscards = new ArrayList<FieldFX>();
		moneyFields = new ArrayList<FieldFX>();
		pointFields = new ArrayList<FieldFX>();
		actionFields = new ArrayList<FieldFX>();
		playerHandFull = new ArrayList<ArrayList<FieldFX>>();
		playerPlayedFull = new ArrayList<ArrayList<FieldFX>>();

		// Main pane
		StackPane mainPane = new StackPane();
		mainPane.setId("mainPane");
		// Initial dynamic bind
		mainPane.minWidthProperty().bind(stage.widthProperty());
		mainPane.maxWidthProperty().bind(stage.widthProperty());
		mainPane.minHeightProperty().bind(stage.minHeightProperty());
		mainPane.maxHeightProperty().bind(stage.minHeightProperty());

		// // Create area for the game and overlay for popups (but don't show it yet)
		VBox vbGame = new VBox();
		bindElement(vbGame, mainPane, 0.95, 0.95);
		overlay = new StackPane();
		overlay.setId("overlay");
		overlay.setVisible(false);
		bindElement(overlay, mainPane, 1.0, 1.0);
		mainPane.getChildren().addAll(vbGame, overlay);

		// Create area other players
		hbOthers = new HBox();
		bindElement(hbOthers, vbGame, FILL, 0.2);

		// Create battlefield
		StackPane paneBF = new StackPane();
		bindElement(paneBF, vbGame, FILL, 0.55);
		HBox hbBF = createbfHBox();
		paneBF.getChildren().add(hbBF);
		bindElement(hbBF, paneBF, FILL, 0.85);

		// Create area player
		HBox hbPlayer = createPlayerHBox();
		bindElement(hbPlayer, vbGame, FILL, 0.2);

		// Create area phase
		StackPane panePhase = createPhasePane();
		bindElement(panePhase, vbGame, FILL, 0.05);

		// Build scene and stage
		vbGame.getChildren().addAll(hbOthers, paneBF, hbPlayer, panePhase);
		Scene scene = new Scene(mainPane);

		// CSS
		scene.getStylesheets().add(getClass().getResource("/client/layout/all.css").toExternalForm());
		bNextPhase.setId("Button");
		bEndTurn.setId("Button");
		bChat.setId("Button");

		stage.setScene(scene);
		bindTexts(scene);
		return scene;
	}

	/**
	 * Builds the area used for the enemy player actions
	 * 
	 * @author Philip Käppeli
	 */
	protected void buildOtherAreas() {
		otherPlayeds.clear();
		otherDiscards.clear();
		hbOthers.getChildren().clear();
		int count = 0;
		for (int i = 0; i < model.getMAX_PLAYERS(); i++) {
			// Exclude the player himself
			if (i != model.getPlayerIndex()) {
				// VBox other
				VBox vbOther = new VBox();
				bindElement(vbOther, hbOthers, 1.0 / (model.getMAX_PLAYERS() - 1), FILL);
				// Fill fields for the current other players, leave the rest blank
				if (count < model.getOtherPlayers().size()) {
					// Player name
					Label lblPlayerName = new Label(model.getOtherPlayers().get(count));
					lblPlayerName.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase20.asString()));
					lblPlayerName.setId("otherTexts");
					lblPlayerName.setAlignment(Pos.CENTER);
					// Visible card area
					StackPane spOther = new StackPane();
					spOther.setId("StackPane");
					HBox hbOther = new HBox();
					// fill area
					buildOtherFields(hbOther, i);
					count++;
					spOther.getChildren().add(hbOther);
					bindElement(hbOther, spOther, 0.6, FILL);
					vbOther.getChildren().addAll(lblPlayerName, spOther);
					bindElement(lblPlayerName, vbOther, FILL, 0.2);
					bindElement(spOther, vbOther, FILL, 0.8);
				}
				hbOthers.getChildren().add(vbOther);
			}
		}
	}

	/**
	 * Create the area for the battlefield stacks, includes elements: moneyStacks,
	 * pointStacks, actionStacks
	 * 
	 * @author Philip Käppeli
	 * @return HBox battlefield
	 */
	private HBox createbfHBox() {
		HBox hbBF = new HBox();

		// VBox money stacks
		vbMoneyStacks = new VBox();
		bindElement(vbMoneyStacks, hbBF, 0.12, FILL);

		// VBox point stacks
		vbPointStacks = new VBox();
		bindElement(vbPointStacks, hbBF, 0.12, FILL);

		// StackPane action stacks
		StackPane spAction = new StackPane();
		bindElement(spAction, hbBF, 0.6, FILL);
		// > GridPane action stacks
		gpAction = new GridPane();
		spAction.getChildren().add(gpAction);
		bindElement(gpAction, spAction, 0.85, FILL);

		// StackPane hoverCard
		spHoverCard = new StackPane();
		bindElement(spHoverCard, hbBF, 0.16, FILL);
		// > FieldFX hoverCard
		hoverCard = new FieldFX("empty", false, 1);
		hoverCard.setVisible(false);
		hoverCard.setLocked(true);
		spHoverCard.getChildren().add(hoverCard);
		bindElement(hoverCard, spHoverCard, 1.0, 1.0);

		hbBF.getChildren().addAll(vbMoneyStacks, vbPointStacks, spAction, spHoverCard);
		return hbBF;
	}

	/**
	 * Show hoverCard-effect
	 * 
	 * @author Philip Käppeli
	 * @param cardName
	 */
	protected void displayHoverCard(String cardName) {
		hoverCard.setVisible(true);
		hoverCard.setName(cardName);
	}

	/**
	 * Stop showing the hoverCard-effect
	 * 
	 * @author Philip Käppeli
	 */
	protected void hideHoverCard() {
		hoverCard.setVisible(false);
	}

	/**
	 * Create the area for the player stacks Includes elements: drawStack,
	 * playerHand, playedCards, discardStack
	 * 
	 * @author Philip Käppeli
	 * @return HBox player
	 */
	private HBox createPlayerHBox() {
		HBox hbPlayer = new HBox();

		// player draw stack
		StackPane drawPane = new StackPane();
		bindElement(drawPane, hbPlayer, 0.15, FILL);

		// > FieldFX
		drawField = new FieldFX("draw", true, 0);
		drawField.setLocked(false);
		bindElement(drawField, drawPane, 0.55, 1);
		drawPane.getChildren().add(drawField);

		// player hand
		StackPane handPane = new StackPane();
		bindElement(handPane, hbPlayer, 0.35, FILL);

		// > HBox player hand
		hbHand = new HBox();
		bindElement(hbHand, handPane, 0.9, 0.8);
		handPane.getChildren().add(hbHand);

		// > > button left
		btnPlayerHandLeft = new Button();
		bindElement(btnPlayerHandLeft, hbHand, 0.075, FILL);
		hbHand.getChildren().add(btnPlayerHandLeft);
		btnPlayerHandLeft.setVisible(false);

		// > > button right
		btnPlayerHandRight = new Button();
		bindElement(btnPlayerHandRight, hbHand, 0.075, FILL);
		hbHand.getChildren().add(btnPlayerHandRight);
		btnPlayerHandRight.setVisible(false);

		// player played
		StackPane playedPane = new StackPane();
		bindElement(playedPane, hbPlayer, 0.35, FILL);

		// > HBox player played
		hbPlayed = new HBox();
		bindElement(hbPlayed, handPane, 0.9, 0.8);
		playedPane.getChildren().add(hbPlayed);

		// > > button left
		btnPlayerPlayedLeft = new Button();
		bindElement(btnPlayerPlayedLeft, hbPlayed, 0.075, FILL);
		hbPlayed.getChildren().add(btnPlayerPlayedLeft);
		btnPlayerPlayedLeft.setVisible(false);

		// > > button right
		btnPlayerPlayedRight = new Button();
		bindElement(btnPlayerPlayedRight, hbPlayed, 0.075, FILL);
		hbPlayed.getChildren().add(btnPlayerPlayedRight);
		btnPlayerPlayedRight.setVisible(false);

		// player discard stack
		StackPane discardPane = new StackPane();
		bindElement(discardPane, hbPlayer, 0.15, FILL);

		// > FieldFX
		discardField = new FieldFX("empty", true, 0);
		bindElement(discardField, discardPane, 0.55, FILL);
		discardField.setLocked(true);
		discardPane.getChildren().add(discardField);

		hbPlayer.getChildren().addAll(drawPane, handPane, playedPane, discardPane);

		drawPane.setId("StackPane");
		handPane.setId("StackPane");
		playedPane.setId("StackPane");
		discardPane.setId("StackPane");
		btnPlayerHandLeft.setId("ButtonLeftPage");
		btnPlayerHandRight.setId("ButtonRightPage");
		btnPlayerPlayedLeft.setId("ButtonLeftPage");
		btnPlayerPlayedRight.setId("ButtonRightPage");
		return hbPlayer;
	}

	/**
	 * Create the area for handling the player phase Includes elements: TextField
	 * currentPhase, button nextPhase, button endTurn
	 * 
	 * @author Philip Käppeli
	 * @return StackPane player phase
	 */
	private StackPane createPhasePane() {
		Translator t = ServiceLocator.getServiceLocator().getTranslator();
		StackPane paneButtons = new StackPane();
		// HBox player buttons
		HBox hbButtons = new HBox();
		bindElement(hbButtons, paneButtons, 0.99, 0.7);
		// Phase label
		tfPhase = new TextField();
		tfPhase.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase13.asString()));
		tfPhase.setText(model.getCurrentPhase().get());
		tfPhase.setDisable(true);
		bindElement(tfPhase, hbButtons, 0.2, 1.0);
		// Buttons
		bNextPhase = new Button(t.getString("game.nextPhase"));
		bNextPhase.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		bNextPhase.setDisable(true);
		bindElement(bNextPhase, hbButtons, 0.1, 1.0);
		bEndTurn = new Button(t.getString("game.endTurn"));
		bEndTurn.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		bEndTurn.setDisable(true);
		bindElement(bEndTurn, hbButtons, 0.1, 1.0);
		bChat = new Button(t.getString("game.chat"));
		bChat.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		bindElement(bChat, hbButtons, 0.1, 1.0);

		hbButtons.getChildren().addAll(tfPhase, bNextPhase, bEndTurn, bChat);
		paneButtons.getChildren().add(hbButtons);

		paneButtons.setId("StackPane");
		return paneButtons;
	}

	/**
	 * Fill the area for a specific enemy player
	 * 
	 * @author Philip Käppeli
	 * @param hbOther
	 * @param currIndex
	 */
	private void buildOtherFields(HBox hbOther, int currIndex) {
		Translator t = ServiceLocator.getServiceLocator().getTranslator();
		hbOther.getChildren().clear();
		// VBox other player "last played"
		VBox vbPlayed = new VBox();
		bindElement(vbPlayed, hbOther, 0.5, FILL);

		// > Last played card of other player
		StackPane otherPlayed = new StackPane();
		bindElement(otherPlayed, vbPlayed, FILL, 0.8);

		// > > FieldFX
		if (model.getOtherPlayed().size() > currIndex) { // to avoid a weird exception I cannot explain
			FieldFX fp = new FieldFX(model.getOtherPlayed().get(currIndex), false, 0);
			otherPlayeds.add(fp);
			fp.setLocked(true);
			bindElement(fp, otherPlayed, 0.6, FILL);
			otherPlayed.getChildren().add(fp);
		}

		// > Label "last played"
		Label lblPlayed = new Label(t.getString("game.lastPlayed"));
		lblPlayed.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		lblPlayed.setAlignment(Pos.CENTER);
		bindElement(lblPlayed, vbPlayed, FILL, 0.2);

		vbPlayed.getChildren().addAll(otherPlayed, lblPlayed);

		// VBox other player "discard stack"
		VBox vbStack = new VBox();
		bindElement(vbStack, hbOther, 0.5, FILL);

		// > Top card of other players discard stack
		StackPane otherStack = new StackPane();
		bindElement(otherStack, vbStack, FILL, 0.8);

		// > > FieldFX
		if (model.getOtherDiscard().size() > currIndex) { // to avoid a weird error when leaving instantly after gamestart
			FieldFX fs = new FieldFX(model.getOtherDiscard().get(currIndex), false, 0);
			otherDiscards.add(fs);
			fs.setLocked(true);
			bindElement(fs, otherStack, 0.6, FILL);
			otherStack.getChildren().add(fs);
		}

		// > Label "discard stack"
		Label lblStack = new Label(t.getString("game.discardStack"));
		lblStack.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		lblStack.setAlignment(Pos.CENTER);
		bindElement(lblStack, vbStack, FILL, 0.2);

		vbStack.getChildren().addAll(otherStack, lblStack);
		hbOther.getChildren().addAll(vbPlayed, vbStack);

		otherPlayed.setId("StackPane");
		lblPlayed.setId("otherTexts");
		otherStack.setId("StackPane");
		lblStack.setId("otherTexts");
	}

	/**
	 * Fill the battlefield with card stacks
	 * 
	 * @author Philip Käppeli
	 */
	protected void buildBattleField() {
		vbMoneyStacks.getChildren().clear();
		vbPointStacks.getChildren().clear();
		actionFields.clear();

		// > Panes money stacks
		for (String cardName : model.getMoneyCards()) {
			// > > money stack
			StackPane p = new StackPane();
			p.setId("StackPane");
			bindElement(p, vbMoneyStacks, FILL, 1.0 / model.getMoneyCards().size());
			vbMoneyStacks.getChildren().add(p);

			// > > > FieldFX
			FieldFX f = new FieldFX(cardName, true, model.getBFStackSize(cardName));
			bindElement(f, p, 0.6, 0.9);
			p.getChildren().add(f);
			moneyFields.add(f);
		}

		// > Panes point stacks
		for (String cardName : model.getPointCards()) {
			// > > point stack
			StackPane p = new StackPane();
			p.setId("StackPane");
			bindElement(p, vbPointStacks, FILL, 1.0 / model.getPointCards().size());
			vbPointStacks.getChildren().add(p);

			// > > > FieldFX
			FieldFX f = new FieldFX(cardName, true, model.getBFStackSize(cardName));
			bindElement(f, p, 0.6, 0.9);
			p.getChildren().add(f);
			pointFields.add(f);
		}

		// > > action stack
		for (int i = 0; i < model.getSelActionCards().size(); i++) {
			StackPane p = new StackPane();
			p.setId("StackPane");
			bindElement(p, gpAction, FILL / MAX_ACTIONROWS, FILL / 2.0);
			gpAction.add(p, (i % MAX_ACTIONROWS), i / MAX_ACTIONROWS);

			// > > > FieldFX
			FieldFX f = new FieldFX(model.getSelActionCards().get(i), true,
					model.getBFStackSize(model.getSelActionCards().get(i)));
			bindElement(f, p, 0.8, 0.9);
			p.getChildren().add(f);
			actionFields.add(f);
		}
	}

	/**
	 * Build ArrayLists used for player hand, split in pages of MAX_HANDSIZE cards
	 * per page
	 * 
	 * @author Philip Käppeli
	 * @param handSize
	 */
	protected void buildPlayerHand(int handSize) {
		playerHandFull.clear();
		// create player hand pages
		for (int i = 0; i < (handSize / model.getMAX_PAGESIZE()) + 1; i++) {
			ArrayList<FieldFX> playerHand = new ArrayList<FieldFX>();
			playerHandFull.add(playerHand);
		}
		if (handSize == model.getMAX_PAGESIZE()) {
			model.setNumHandPages(1);
		} else {
			model.setNumHandPages((handSize / model.getMAX_PAGESIZE()) + 1);
		}

		// fill the pages
		for (int i = 0; i < handSize; i++) {
			FieldFX f = new FieldFX(model.getPlayerHand().get(i), false, 1);
			bindElement(f, hbHand, 0.17, FILL);
			playerHandFull.get(i / model.getMAX_PAGESIZE()).add(f);
		}
	}

	/**
	 * Clear player hand HBox, show another page
	 * 
	 * @author Philip Käppeli
	 * @param pageNum
	 */
	protected void showPlayerHand(int pageNum) {
		hbHand.getChildren().clear();
		hbHand.getChildren().add(btnPlayerHandLeft);
		for (FieldFX f : playerHandFull.get(pageNum)) {
			hbHand.getChildren().add(f);
		}
		hbHand.getChildren().add(btnPlayerHandRight);
	}

	/**
	 * Build the pages for the players played cards and fill them
	 * 
	 * @author Philip Käppeli
	 * @param playedSize
	 */
	protected void buildPlayerPlayed(int playedSize) {
		playerPlayedFull.clear();
		// create player hand pages
		for (int i = 0; i < (playedSize / model.getMAX_PAGESIZE()) + 1; i++) {
			ArrayList<FieldFX> playerPlayed = new ArrayList<FieldFX>();
			playerPlayedFull.add(playerPlayed);
		}
		if (playedSize == model.getMAX_PAGESIZE()) {
			model.setNumPlayedPages(1);
		} else {
			model.setNumPlayedPages((playedSize / model.getMAX_PAGESIZE()) + 1);
		}

		// fill the pages
		for (int i = 0; i < playedSize; i++) {
			FieldFX f = new FieldFX(model.getPlayerPlayed().get(i), false, 0);
			playerPlayedFull.get(i / model.getMAX_PAGESIZE()).add(f);
			bindElement(f, hbPlayed, 0.17, FILL); // Card
		}
	}

	/**
	 * Display the current page of the players played cards
	 * 
	 * @author Philip Käppeli
	 * @param pageNum
	 */
	protected void showPlayerPlayed(int pageNum) {
		hbPlayed.getChildren().clear();
		hbPlayed.getChildren().add(btnPlayerPlayedLeft);
		for (FieldFX f : playerPlayedFull.get(pageNum)) {
			hbPlayed.getChildren().add(f);
		}
		hbPlayed.getChildren().add(btnPlayerPlayedRight);
	}

	/**
	 * Show a message (buy/play denied or game cancelled).
	 * 
	 * @author Philip Käppeli
	 * @param popupType
	 */
	protected void showMessageOverlay(String message, boolean exitOnOk) {
		Translator t = ServiceLocator.getServiceLocator().getTranslator();
		overlay.getChildren().clear();
		StackPane sp = new StackPane();
		VBox vBox = new VBox();
		// Message
		Label lblMessage = new Label(message);
		lblMessage.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		// Button ok
		Button bOk = new Button(t.getString("game.ok"));
		bOk.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		bOk.setOnAction((e) -> {
			if (!exitOnOk) {
				// Just hide the message
				overlay.setVisible(false);
				overlay.getChildren().clear();
				model.getDenyMessage().set("");
			} else {
				// Game cancelled -> return to the lobby
				openMainMenu();
			}
		});
		vBox.getChildren().addAll(lblMessage, bOk);
		sp.getChildren().addAll(vBox);
		overlay.getChildren().add(sp);
		overlay.setVisible(true);
		bindElement(sp, overlay, 0.28, 0.16); // Message pane
		bindElement(vBox, sp, 0.8, 0.8);
		bindElement(lblMessage, vBox, 1.0, 0.65); // Message
		bindElement(bOk, vBox, 1.0, 0.35); // Button ok

		sp.setId("overlaySP");
		bOk.setId("Button");
	}

	/**
	 * Show scorelist and timer until the client gets returned to the main menu
	 * 
	 * @author Philip Käppeli
	 * @param scorelist
	 */
	protected void showEndgameOverlay(String[] scorelist) {
		Translator t = ServiceLocator.getServiceLocator().getTranslator();
		overlay.getChildren().clear();
		StackPane sp = new StackPane();
		VBox vBox = new VBox();
		// Title
		Label lblTitle = new Label(t.getString("game.gameEnded"));
		lblTitle.setAlignment(Pos.CENTER);
		vBox.getChildren().add(lblTitle);
		// Scorelist
		StackPane p = new StackPane();
		p.setId("StackPane");
		vBox.getChildren().add(p);
		VBox v = new VBox();
		p.getChildren().add(v);
		for (String entry : scorelist) {
			// Parse scorelist entry
			HBox hBoxEntry = new HBox();
			String[] entrySplit = entry.split(";");
			Label lblName = new Label(entrySplit[0]);
			lblName.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
			Label lblScore = new Label(entrySplit[1] + " " + t.getString("game.points"));
			lblScore.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
			lblScore.setAlignment(Pos.CENTER_RIGHT);
			hBoxEntry.getChildren().addAll(lblName, lblScore);
			v.getChildren().add(hBoxEntry);
			// Bind element sizes
			bindElement(lblName, hBoxEntry, 0.7, 1.0); // Label name
			bindElement(lblScore, hBoxEntry, 0.3, 1.0); // Label score
			bindElement(hBoxEntry, v, 1.0, 1.0 / scorelist.length);
		}
		// Timer
		Label lblTime = new Label("" + model.getEndCountDown().get());
		lblTime.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));
		lblTime.setAlignment(Pos.CENTER);
		model.getEndCountDown().addListener((c) -> {
			lblTime.setText("" + model.getEndCountDown().get());
			if (model.getEndCountDown().get() == 0) {
				// Game ended, return to main menu
				openMainMenu();
			}
		});
		vBox.getChildren().add(lblTime);
		sp.getChildren().add(vBox);
		overlay.getChildren().add(sp);
		overlay.setVisible(true);
		// Bind element sizes
		bindElement(sp, overlay, 0.22, 0.22); // Message pane
		bindElement(vBox, sp, 0.8, 0.8);
		bindElement(lblTitle, vBox, 1.0, 0.2); // Title
		bindElement(p, vBox, 1.0, 0.6); // Scorelist
		bindElement(v, p, 1.0, 0.9);
		bindElement(lblTime, vBox, 1.0, 0.2); // Timer

		sp.setId("overlaySP");
		lblTime.setId("timerText");
	}

	/**
	 * Close the game view and return to the main menu
	 * 
	 * @author Philip Käppeli
	 */
	private void openMainMenu() {
		Platform.runLater(() -> {
			// Open the main menu
			MenuModel mModel = new MenuModel();
			mModel.setUsername(model.getUsername());
			mModel.setHostname(model.getHostname());
			mModel.setPort(model.getPort());
			MainMenuView mmView = new MainMenuView(new Stage(), mModel);
			mmView.getStage().setHeight(getStage().getHeight());
			mmView.getStage().setWidth(getStage().getWidth());
			mmView.getStage().setX(getStage().getX());
			mmView.getStage().setY(getStage().getY());
			new MainMenuController(mModel, mmView);
			mmView.start();
			// Close the chat if open
			if (GameController.chView != null) {
				GameController.chView.stop();
				GameController.chView = null;
			}
			// Close the game view
			stop();
		});
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

		fontSizeBase15
				.bind((sc.widthProperty().add(sc.heightProperty())).divide(screenwidth + screenheight).multiply(15));

		fontSizeBase20
				.bind((sc.widthProperty().add(sc.heightProperty())).divide(screenwidth + screenheight).multiply(20));
	}

	/**
	 * Bind elements to their parents for dynamic resizing
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
