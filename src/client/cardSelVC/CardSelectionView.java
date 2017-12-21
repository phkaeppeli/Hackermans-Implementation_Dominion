package client.cardSelVC;

import java.util.ArrayList;
import client.ServiceLocator;
import client.commonClasses.Translator;
import client.modelClasses.MenuModel;
import client.resourceClasses.FieldFX;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import shared.abstractClasses.View;

/**
 * 
 * @author Philipp Lehmann
 *
 */
public class CardSelectionView extends View<MenuModel> {
	protected Stage hs_stage;
	protected Scene scene;
	protected BorderPane root;
	protected BorderPane pane_inner;
	protected ScrollPane scrollpane;
	protected TilePane tilepane;
	protected HBox hbox_title;
	protected HBox hbox_buttons;
	protected Label lblTitle;
	protected Button btnClose;
	protected final int NUM_CARDS = 9;
	protected final double FILL = 1.0;
	protected DoubleProperty fontSizeBase30;
	protected DoubleProperty fontSizeBase40;
	protected ArrayList<FieldFX> availActionCards;

	/**
	 * CONSTRUCTOR
	 * 
	 * @author Philipp Lehmann
	 *
	 */
	public CardSelectionView(Stage stage, MenuModel model) {
		super(stage, model);
	}

	/**
	 * Set up dynamic GUI of CARD SELECTION VIEW
	 * 
	 * @author Philipp Lehmann
	 *
	 */
	@Override
	protected Scene create_GUI() {
		fontSizeBase30 = new SimpleDoubleProperty();
		fontSizeBase40 = new SimpleDoubleProperty();

		// Getting Translator
		ServiceLocator sl = ServiceLocator.getServiceLocator();
		Translator t = sl.getTranslator();

		// Initialize
		root = new BorderPane();
		scene = new Scene(root);
		pane_inner = new BorderPane();
		scrollpane = new ScrollPane();
		tilepane = new TilePane();
		hbox_title = new HBox();
		hbox_buttons = new HBox();
		btnClose = new Button();
		lblTitle = new Label();
		availActionCards = new ArrayList<FieldFX>();
		
		// bind all elements to their parents
		bindElement(pane_inner, root, 1.0, 1.0);		
		bindElement(hbox_title, pane_inner, 0.9, 0.1);		
		bindElement(hbox_buttons, pane_inner, 0.9, 0.1);
		bindElement(scrollpane, pane_inner, 0.9, 0.7);
		bindElementScroll(tilepane, scrollpane, 0.95);
		bindElement(lblTitle, hbox_title, FILL, FILL);		

		/* Available Cards are added to local array
		 * and dynamically displayed
		 */
		for (String cardName : model.getAvailActionCards()) {
			FieldFX f = new FieldFX(cardName, true, 1);
			bindElement(f, root, 0.2, 0.3);
			tilepane.getChildren().add(f);
			availActionCards.add(f);		
		}

		// set up translatable Texts
		btnClose.setText(t.getString("cardselection.btn_close"));
		lblTitle.setText(t.getString("cardselection.lbl_cs"));
		
		// set IDs for CSS formatting
		pane_inner.setId("pane_inner");
		hbox_title.setId("hbox_title");
		scrollpane.setId("scrollpane");
		tilepane.setId("tilepane");
		btnClose.setId("Button");
		lblTitle.setId("lblTitle");

		// GET CSS FORMATTING
		scene.getStylesheets().add(getClass().getResource("/client/layout/all.css").toExternalForm());

		// add content to Panes
		pane_inner.setTop(hbox_title);
		pane_inner.setCenter(scrollpane);
		pane_inner.setBottom(hbox_buttons);
		scrollpane.setContent(tilepane);
		hbox_title.getChildren().add(lblTitle);
		hbox_buttons.getChildren().addAll(btnClose);

		// Formatting of TilePane
		tilepane.setHgap(10);
		tilepane.setVgap(10);
		tilepane.setAlignment(Pos.CENTER);

		// Setting up Primary Stage
		stage.setTitle("Dominion");
		stage.setWidth(700.0);
		stage.setHeight(850.0);
		stage.centerOnScreen();
		
		root.setCenter(pane_inner);
		stage.setScene(scene);
		bindTexts(scene);
		return scene;
	}

	/**
	 * bind text size
	 * 
	 * @author Philip Käppeli
	 *
	 */
	private void bindTexts(Scene sc) {
		// Source: stackoverflow.com/questions/23705654
		// Source: docs.oracle.com/javafx/2/api/javafx/stage/Screen.html
		double screenwidth = Screen.getPrimary().getVisualBounds().getWidth();
		double screenheight = Screen.getPrimary().getVisualBounds().getHeight();

		fontSizeBase30.bind((sc.widthProperty().add(sc.heightProperty())).divide(screenwidth + screenheight).multiply(30));
		btnClose.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase30.asString()));

		fontSizeBase40.bind((sc.widthProperty().add(sc.heightProperty())).divide(screenwidth + screenheight).multiply(40));
		lblTitle.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase40.asString()));
	}

	/**
	 * bind elements for dynamic resizing
	 * 
	 * @author Philip Käppeli
	 *
	 */
	private void bindElement(Region child, Region parent, double widthMulti, double heightMulti) {
		child.minWidthProperty().bind(parent.widthProperty().multiply(widthMulti));
		child.maxWidthProperty().bind(parent.widthProperty().multiply(widthMulti));
		child.minHeightProperty().bind(parent.heightProperty().multiply(heightMulti));
		child.maxHeightProperty().bind(parent.heightProperty().multiply(heightMulti));
	}
	
	/**
	 * bind elements for dynamic resizing
	 * 
	 * @author Philip Käppeli
	 *
	 */
	private void bindElementScroll(Region child, Region parent, double widthMulti) {
		child.minWidthProperty().bind(parent.widthProperty().multiply(widthMulti));
		child.maxWidthProperty().bind(parent.widthProperty().multiply(widthMulti));
	}
}