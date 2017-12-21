package client.highScoresVC;

import client.ClientMain;
import client.ServiceLocator;
import client.commonClasses.Translator;
import client.modelClasses.MenuModel;
import client.resourceClasses.ListEntry;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import shared.abstractClasses.View;

/**
 * 
 * @author Philipp Lehmann
 *
 */

public class HighScoreView extends View<MenuModel> {
	protected Stage hs_stage;
	protected Scene scene;
	protected BorderPane root;
	protected BorderPane pane_inner;
	protected HBox hbox_title;
	protected HBox hbox_buttons;
	protected Label lblTitle;
	protected Button btnBack;
	protected TableView<ListEntry> table;
	protected ClientMain main;
	final double FILL = 1.0;
	protected DoubleProperty fontSizeBase12;
	protected DoubleProperty fontSizeBase15;
	protected DoubleProperty fontSizeBase20;

	public HighScoreView(Stage stage, MenuModel model) {
		super(stage, model);
	}

	
	/**
	 * Set up dynamic GUI of HIGH SCORE VIEW
	 * 
	 * @author Philipp Lehmann
	 *
	 */
	@Override
	protected Scene create_GUI() {
		fontSizeBase12 = new SimpleDoubleProperty();
		fontSizeBase15 = new SimpleDoubleProperty();
		fontSizeBase20 = new SimpleDoubleProperty();

		ServiceLocator sl = ServiceLocator.getServiceLocator();
		Translator t = sl.getTranslator();

		// Initialize
		root = new BorderPane();
		scene = new Scene(root);
		pane_inner = new BorderPane();
		hbox_title = new HBox();
		hbox_buttons = new HBox();
		table = new TableView<ListEntry>();
		lblTitle = new Label();
		btnBack = new Button();
		
		// bind all elements to their parents
		bindElement(pane_inner, root, 0.4, 0.55);
		bindElement(hbox_title, pane_inner, 0.9, 0.2);
		bindElement(hbox_buttons, pane_inner, FILL, 0.1);
		bindElement(lblTitle, hbox_title, FILL, FILL);
		bindElement(btnBack, hbox_buttons, 0.2, 1.1);

		// setting up translatable Texts		
		lblTitle.setText(t.getString("highscore.lbl_highscore"));		
		btnBack.setText(t.getString("highscore.btn_back"));

		// ADD columns to table view
		TableColumn<ListEntry, String> position = new TableColumn<ListEntry, String>(t.getString("highscore.tc_position"));
		TableColumn<ListEntry, String> name = new TableColumn<ListEntry, String>(t.getString("highscore.tc_name"));
		TableColumn<ListEntry, String> points = new TableColumn<ListEntry, String>(t.getString("highscore.tc_points"));
		
		position.setCellValueFactory(new PropertyValueFactory<ListEntry, String>("rank"));
		name.setCellValueFactory(new PropertyValueFactory<ListEntry, String>("name"));
		points.setCellValueFactory(new PropertyValueFactory<ListEntry, String>("parameter"));	

		position.setSortable(false);
		name.setSortable(false);
		points.setSortable(false);
		
		// customize table view
		table.setEditable(false);
		table.setSelectionModel(null);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setPlaceholder(new Label(""));

		// set IDs for CSS
		root.setId("root_Highscore");
		pane_inner.setId("pane_inner");
		hbox_title.setId("hbox_title");
		btnBack.setId("Button");
		lblTitle.setId("lblTitle");
		table.setId("tableview");
		
		// GET CSS FORMATTING
		scene.getStylesheets().add(getClass().getResource("/client/layout/all.css").toExternalForm());

		// add content Panes and table
		pane_inner.setTop(hbox_title);
		pane_inner.setCenter(table);
		pane_inner.setBottom(hbox_buttons);
		table.getColumns().addAll(position, name, points);
		hbox_title.getChildren().add(lblTitle);
		hbox_buttons.getChildren().addAll(btnBack);

		// Setting up Borderpane root for dynamic Handling
		root.minWidthProperty().bind(stage.widthProperty());
		root.maxWidthProperty().bind(stage.widthProperty());
		root.minHeightProperty().bind(stage.minHeightProperty());
		root.maxHeightProperty().bind(stage.minHeightProperty());

		// Setting up Primary Stage
		stage.setTitle("Dominion");
		
		root.setCenter(pane_inner);
		stage.setScene(scene);
		bindTexts(scene);
		return scene;
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

		fontSizeBase12
				.bind((sc.widthProperty().add(sc.heightProperty())).divide(screenwidth + screenheight).multiply(12));
		table.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase12.asString()));

		fontSizeBase15
				.bind((sc.widthProperty().add(sc.heightProperty())).divide(screenwidth + screenheight).multiply(15));
		btnBack.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBase15.asString()));

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
