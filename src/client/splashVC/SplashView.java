package client.splashVC;

import client.modelClasses.MenuModel;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.abstractClasses.View;

/**
 * 
 * @author Philipp Lehmann
 *
 */
public class SplashView extends View<MenuModel>{
	protected Scene scene;
	protected BorderPane root;
	protected BorderPane pane_inner;
	protected HBox hbox_title;
	protected VBox vbox_pb;
	
	ProgressBar pb_progress;
	Image img;
	Label lbl_titlepicture;
	final double FILL = 1.0;
	
	/**
	 * CONSTRUCTOR
	 * 
	 * @author Philipp Lehmann
	 * @param stage
	 * @param model
	 * 
	 */
	public SplashView(Stage stage, MenuModel model) {
		super(stage, model);
	}

	/**
	 * Set up dynamic GUI of SPLASH VIEW
	 * 
	 * @author Philipp Lehmann
	 *
	 */
	@Override
	protected Scene create_GUI() {
		// Initialize
		root = new BorderPane();
		scene = new Scene(root);
		pane_inner = new BorderPane();
		hbox_title = new HBox();
		vbox_pb = new VBox();
		pb_progress = new ProgressBar();
		lbl_titlepicture = new Label();
		
		// bind elements to their parents
		bindElement(pane_inner, root, 0.3, 0.4);
		bindElement(hbox_title, pane_inner, FILL, 0.5);
		bindElement(vbox_pb, pane_inner, FILL, 0.5);
		bindElement(pb_progress, vbox_pb, 0.8, 0.1);
		bindElement(lbl_titlepicture, hbox_title, FILL, FILL);
		
		// Setting up Borderpane root for dynamic Handling
		root.setCenter(pane_inner);
		root.minWidthProperty().bind(stage.widthProperty());
		root.maxWidthProperty().bind(stage.widthProperty());
		root.minHeightProperty().bind(stage.minHeightProperty());
		root.maxHeightProperty().bind(stage.minHeightProperty());

		// Add content to Panes
		pane_inner.setTop(hbox_title);
		pane_inner.setCenter(vbox_pb);
		hbox_title.getChildren().add(lbl_titlepicture);
		vbox_pb.getChildren().addAll(pb_progress);
		
		// Set IDs for CSS formatting
		pb_progress.setId("pb_progress");
		pane_inner.setId("pane_inner");
		hbox_title.setId("hbox_titlepicture");
		vbox_pb.setId("vbox_pb");
		lbl_titlepicture.setId("lbl_titlepicture");
		
		// GET CSS FORMATTING
		scene.getStylesheets().add(getClass().getResource("/client/layout/all.css").toExternalForm());
		
		// Setting up Primary Stage
		stage.setTitle("Dominion");
		stage.setScene(scene);
		
		return scene;
	}

	// Bind elements for dynamic resizing
	private void bindElement(Region child, Region parent, double widthMulti, double heightMulti) {
		child.minWidthProperty().bind(parent.widthProperty().multiply(widthMulti));
		child.maxWidthProperty().bind(parent.widthProperty().multiply(widthMulti));
		child.minHeightProperty().bind(parent.heightProperty().multiply(heightMulti));
		child.maxHeightProperty().bind(parent.heightProperty().multiply(heightMulti));
	}
}