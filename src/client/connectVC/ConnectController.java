package client.connectVC;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import client.modelClasses.MenuModel;
import client.resourceClasses.ClientConnector;
import client.splashVC.SplashController;
import client.splashVC.SplashView;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import shared.abstractClasses.Controller;
import shared.resourceClasses.MessageType;

/**
 * Controller Class for Connect MVC, Checks if IP and Port is Correct.
 * 
 * @author Pascal Moll
 *
 */

public class ConnectController extends Controller<MenuModel, ConnectView>{
	private ClientConnector cc;
	
	/**
	 * Constructor: EventHandlers for Buttons, Handles wrong IP-Address or Port Number. Leads To Splashscreen if correct.
	 * 
	 * @author Pascal Moll
	 * @param model
	 * @param view
	 */
	public ConnectController(MenuModel model, ConnectView view) {
		super(model, view);
		
		//Set Connect Button on Action
		view.btn_connect.setOnAction((event)-> {
			//If - else checks if Port and IP are entered in a correct layout of a IP adress and Port
			if(ConnectController.testIPAdress(view.txtf_lp.getText()) && ConnectController.testPort(view.txtf_port.getText())){
				handshake(view.txtf_lp.getText(), view.txtf_port.getText());
				view.btn_connect.setDisable(true);
			} else {
				//if - else checks which of the inserted parameter are wrong
				if(!ConnectController.testIPAdress(view.txtf_lp.getText())){
					System.out.println("Falsch IP");
					view.txtf_lp.setStyle("-fx-text-inner-color: RED;");
				} 
				if(!ConnectController.testPort(view.txtf_port.getText())){
					System.out.println("FALSCH PORT");
					view.txtf_port.setStyle("-fx-text-inner-color: RED;");
				}
			}
		});
		
		
		// Wait for the socket to either create a connection or fail
		model.connectionAccepted.addListener((c) -> {
			Platform.runLater(() -> {
				if (model.connectionAccepted.get() == -1) {
					// Connecting failed, let the user know that something wrong
					// was entered
					view.txtf_lp.setStyle("-fx-text-inner-color: RED;");
					view.txtf_port.setStyle("-fx-text-inner-color: RED;");
					view.btn_connect.setDisable(false);
				} else {
					// Connection successful, move on to the splash screen
					cc.disconnect();
					SplashView spView = new SplashView(new Stage(), model);
					spView.getStage().setHeight(view.getStage().getHeight());
					spView.getStage().setWidth(view.getStage().getWidth());
					spView.getStage().setX(view.getStage().getX());
					spView.getStage().setY(view.getStage().getY());
					new SplashController(model, spView);
					spView.start();
					view.stop();
					model.initialize();
				}
			});
		});
		
		/**
		 * The user can continue with enter
		 * @author User Peter, https://stackoverflow.com/questions/37648222/how-can-i-detect-the-space-keyevent-anywhere-in-my-javafx-app
		 */
		view.root.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if(event.getCode() == KeyCode.ENTER) {
				view.btn_connect.fire();
			}
		});
	}
	
	/**
	 * sets Port and Hostname in abstract class Model + sends message for creating connection 
	 * 
	 * @author Pascal Moll
	 * @param ip
	 * @param port
	 */
	public void handshake(String ip, String port) {
		try {
			model.setHostname(ip);
			model.setPort(Integer.parseInt(port));
			cc = new ClientConnector();
			cc.createConnection(MessageType.HANDSHAKE, model);
		} catch (Exception e){
			e.printStackTrace();	
		}
	}
	
	/**
	 * Checks if IP Adress is written in the right format.
	 * 
	 * @author Pascal Moll
	 * @param ipAdress
	 * @return
	 */
	private static Boolean testIPAdress(String ipAdress){
		//Setting Up Regex -> 0.0.0.0 - 255.255.255.255
		String pattern = "((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]?[0-9])[.]){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]?[0-9])";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(ipAdress);
			
		if(m.matches() && !ipAdress.isEmpty()){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if Port number is written in the right format.
	 * 
	 * @author Pascal Moll
	 * @param port
	 * @return
	 */
	private static Boolean testPort(String port){
		//Setting Up Regex -> 0-65535
		String pattern ="^(0|[1-9][0-9]{0,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(port);
			
		if(m.matches() && !port.isEmpty()){
			return true;
		} else {
			return false;
		}
	}
	
	public ClientConnector getClientConnector(){
		return this.cc;
	}
	
	public void setClientConnector(ClientConnector cc){
		this.cc = cc;
	}
}
