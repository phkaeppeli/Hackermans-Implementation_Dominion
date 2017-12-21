package client.resourceClasses;

import client.ServiceLocator;
import client.commonClasses.Translator;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * This class is used as a card "button". It displays the card image to a card
 * name in the currently selected language.
 * 
 * @author Philip Käppeli
 *
 */
public class FieldFX extends Pane {
	// final int INIT_STACKSIZE = 10;
	public String name;
	public int size;
	public boolean showSize;
	private boolean actionDisabled;
	private Pane overlay;
	private String baseStyle;
	private Label lblSize;

	/**
	 * CONSTRUCTOR: Shows or hides the stack size, calls methods to set the
	 * background image and create the overlay used for the click/disabled effect.
	 * 
	 * @author Philip Käppeli
	 * @param name
	 * @param showSize
	 * @param size
	 */
	public FieldFX(String name, boolean showSize, int size) {
		this.name = name;
		this.size = size;
		this.showSize = showSize;
		if (showSize) {
			lblSize = new Label("" + size);
			lblSize.setStyle("-fx-text-fill: white; -fx-background-color: rgb(255, 0, 0, 0.7); -fx-font-size: 15");
			this.getChildren().add(lblSize);
		}
		setBackgroundImage();
		createOverlay();
		this.setId("fieldFX");
	}

	/**
	 * Set background image for the stack according to the field name
	 * 
	 * @author Philip Käppeli
	 */
	private void setBackgroundImage() {
		// Build path to the background image
		Translator t = ServiceLocator.getServiceLocator().getTranslator();
		String path = System.getProperty("user.dir").replace("\\", "/");
		String fullPath = "file:/" + path + "/imagesClient/" + t.getCurrentLocale() + "/" + name + ".jpg";
		// Create style
		this.baseStyle = "-fx-background-image: url('" + fullPath + "'); " + "-fx-background-size: stretch; "
				+ "-fx-background-position: top left;";

		// Set background image
		this.setStyle(baseStyle);
	}

	/**
	 * Create overlay used for the mouseClicked and locked effects
	 * 
	 * @author Philip Käppeli
	 */
	private void createOverlay() {
		this.overlay = new Pane();
		this.overlay.setStyle("-fx-background-color: transparent");

		// bind overlay to the main element
		this.overlay.minWidthProperty().bind(this.widthProperty());
		this.overlay.maxWidthProperty().bind(this.widthProperty());
		this.overlay.minHeightProperty().bind(this.heightProperty());
		this.overlay.maxHeightProperty().bind(this.heightProperty());
		this.getChildren().add(this.overlay);

		// If pressed, show overlay
		this.overlay.setOnMousePressed((event) -> {
			if (!this.actionDisabled) {
				this.overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5)");
			}
		});

		// if released, hide overlay again
		this.overlay.setOnMouseReleased((event) -> {
			if (!this.actionDisabled) {
				this.overlay.setStyle("-fx-background-color: transparent");
			}
		});
	}

	/**
	 * Lock/unlock a button using the overlay and setDisable
	 * 
	 * @author Philip Käppeli
	 * @param lock
	 */
	public void setLocked(boolean lock) {
		// the parameter is true or the stack is empty, lock the fieldfx, else unlock it
		lock = (lock || this.size <= 0);
		if (lock) {
			this.overlay.setStyle("-fx-background-color: rgba(50, 50, 50, 0.5)");
		} else {
			this.overlay.setStyle("-fx-background-color: transparent");
		}
		this.actionDisabled = lock;
	}

	public boolean getActionDisabled() {
		return this.actionDisabled;
	}

	// SETTER name, also re-sets the background image to match the new name
	public void setName(String name) {
		this.name = name;
		setBackgroundImage();
	}

	// SETTER stack size
	public void setStacksize(String size) {
		this.size = Integer.parseInt(size);
		lblSize.setText(size);
	}
}
