package shared.resourceClasses;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

import shared.BFCardStack;

/**
 * 
 * Message class used by both client and server for communication
 * 
 * @author Philip Käppeli
 *
 */
public class GameMessage implements Serializable {
	protected static final long serialVersionUID = 1L; // Default serial id
	protected MessageType type;
	protected String[] parameters;
	protected ArrayList<BFCardStack> stackList;
	protected ArrayList<File> files;
	protected String localeStr;

	/**
	 * Various constructors for the different types of messages
	 * 
	 * @author Philip Käppeli
	 * @param type
	 * @param parameters
	 * @param stackList
	 * @param files
	 * @param localeStr
	 */
	public GameMessage(MessageType type) {
		this.type = type;
	}

	public GameMessage(MessageType type, String... parameters) {
		this.type = type;
		this.parameters = parameters;
	}

	public GameMessage(MessageType type, ArrayList<BFCardStack> stackList) {
		this.type = type;
		this.stackList = stackList;
	}

	public GameMessage(MessageType type, ArrayList<File> files, String localeStr) {
		this.type = type;
		this.files = files;
		this.localeStr = localeStr;
	}

	/**
	 * Send message to server
	 * 
	 * @author Philip Käppeli
	 * @param s
	 */
	public void send(Socket s) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(this);
			out.flush();
		} catch (Exception e) {
			try {
				if (!s.isClosed()) {
					s.close();
				}
			} catch (Exception ex) {
				// do nothing
			}
		}
	}

	/**
	 * Receive message from server
	 * 
	 * @author Philip Käppeli
	 * @param s
	 * @return GameMessage object
	 */
	public static GameMessage receive(Socket s) {
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(s.getInputStream());
			GameMessage msg = (GameMessage) in.readObject();
			return msg;
		} catch (Exception e) {
			// End of connection
			try {
				if (!s.isClosed()) {
					s.close();
				}
			} catch (Exception ex) {
				// do nothing
			}
			return new GameMessage(MessageType.ENDOFCONNECTION);
		}
	}

	/**
	 * GETTERS, SETTERS and TOSTRING
	 */
	public MessageType getType() {
		return this.type;
	}

	public String[] getParameters() {
		return this.parameters;
	}

	public String getLocaleStr() {
		return this.localeStr;
	}

	public ArrayList<File> getFiles() {
		return this.files;
	}

	public ArrayList<BFCardStack> getStackList() {
		return stackList;
	}

	// ToString
	@Override
	public String toString() {
		String msgText = type.toString() + ":";
		if (parameters != null) {
			for (String s : parameters) {
				msgText += " " + s;
			}
		}
		if (stackList != null) {
			for (BFCardStack s : stackList) {
				msgText += " " + s.getCard();
			}
		}
		return msgText;
	}
}
