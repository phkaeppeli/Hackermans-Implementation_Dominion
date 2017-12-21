package server.database;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import shared.cardClasses.ActionCard;
import shared.cardClasses.MoneyCard;
import shared.cardClasses.PointCard;

/**
 * Gets data from the database and server files
 * @author Mario Allemann
 *
 */
public class DataGrabber {

	private DBConnect con;

	public DataGrabber() {
		con = new DBConnect();
	}

	/**
	 * Gets the server version from the database
	 * 
	 * @return Server version
	 * 
	 * @author Mario Allemann
	 */
	public String getVersion() {
		ResultSet RSVersion = execQuery("SELECT version FROM version;");
		String version = null;
		try {
			while (RSVersion.next()) {
				version = RSVersion.getString("version");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return version;

	}

	/**
	 * Takes an MySQL query and returns a DataSet with data
	 * @param query A MySQL SELECT query
	 * @return The result of the query
	 * 
	 * @author Mario Allemann
	 */
	public ResultSet execQuery(String query) {
		Statement statement;
		try {
			statement = con.conn.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			return resultSet;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}


	/**
	 * Gets the top 10 highscores from the database
	 * 
	 * @return An ArrayList with highscores with the format (username;score)
	 * 
	 * @author Mario Allemann
	 */
	public ArrayList<String> getHighscore() {
		String uNameDBColumn = "username";
		String pointsDBColumn = "points";
		// Gets username and points from DB (in order)
		ResultSet highscores = execQuery("SELECT " + uNameDBColumn + "," + pointsDBColumn
				+ " FROM highscore_list ORDER BY points DESC LIMIT 10;");
		
		ArrayList<String> highscoresArr = new ArrayList<String>();
		try {
			// Iterates through rows
			while (highscores.next()) {
				// Gets username and points
				String row = highscores.getString("username") + ";" + highscores.getString("points");
				highscoresArr.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return highscoresArr;

	}

	/**
	 * Adds a new highscore to the database
	 * 
	 * @param username The players name
	 * @param score The players score
	 * 
	 * @author Mario Allemann
	 */
	public void addHighscore(String username, int score) {

		try {
			PreparedStatement statement = con.conn
					.prepareStatement("INSERT INTO highscore_list (username, points) VALUES (? , ?);");
			statement.setString(1, username);
			statement.setInt(2, score);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets all action cards from the database
	 * 
	 * @return An ArrayList with ActionCard objects
	 * 
	 * @author Mario Allemann
	 */
	public ArrayList<CardPair> getActionCards() {
		ResultSet cardsRS = execQuery("SELECT * FROM cards WHERE cardType = 'Action';");
		ArrayList<CardPair> actionCards = new ArrayList<CardPair>();

		try {
			while (cardsRS.next()) {
				CardPair actionCard = new CardPair(new ActionCard(cardsRS.getString("name_EN"), cardsRS.getInt("costs"),
						cardsRS.getInt("plus_Action"), cardsRS.getInt("plus_Card"), cardsRS.getInt("plus_Money"),
						cardsRS.getInt("plus_Buy"), cardsRS.getInt("specialActionID")),
						cardsRS.getInt("defaultStackSize"));
				actionCards.add(actionCard);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return actionCards;
	}

	/**
	 * Gets all money cards from the database
	 * 
	 * @return An ArrayList with MoneyCard objects
	 * 
	 * @author Mario Allemann
	 */
	public ArrayList<CardPair> getMoneyCards() {
		ResultSet cardsRS = execQuery("SELECT * FROM cards WHERE cardType = 'Money';");
		ArrayList<CardPair> moneyCards = new ArrayList<CardPair>();

		try {
			while (cardsRS.next()) {
				CardPair moneyCard = new CardPair(
						new MoneyCard(cardsRS.getString("name_EN"), cardsRS.getInt("costs"), cardsRS.getInt("value")),
						cardsRS.getInt("defaultStackSize"));
				moneyCards.add(moneyCard);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return moneyCards;
	}

	/**
	 * Gets all point cards from the database
	 * 
	 * @return An ArrayList with PointCard objects
	 * 
	 * @author Mario Allemann
	 */
	public ArrayList<CardPair> getPointCards() {
		ResultSet cardsRS = execQuery("SELECT * FROM cards WHERE cardType = 'Point';");
		ArrayList<CardPair> pairCollection = new ArrayList<CardPair>();
		try {
			while (cardsRS.next()) {
				CardPair pointCard = new CardPair(
						new PointCard(cardsRS.getString("name_EN"), cardsRS.getInt("costs"), cardsRS.getInt("points")),
						cardsRS.getInt("defaultStackSize"));
				pairCollection.add(pointCard);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pairCollection;
	}

	/**
	 * Gets all the card images for a specific locale, which then can be sent to the
	 * client
	 * 
	 * @param locale The locale string
	 * @return An ArrayList with card images
	 * 
	 * @author Mario Allemann
	 */
	public ArrayList<File> getAllCardImages(String locale) {
		ArrayList<File> allCardImages = new ArrayList<File>();

		// The folder with the images
		String pathToFiles = System.getProperty("user.dir") + "\\imagesServer\\" + locale + "\\";

		// Adds all files in the folder to the ArrayList
		// Source: User Mike Samuel,
		// https://stackoverflow.com/questions/4917326/how-to-iterate-over-the-files-of-a-certain-directory-in-java
		File directory = new File(pathToFiles);
		File[] directoryListing = directory.listFiles();
		for (File child : directoryListing) {
			allCardImages.add(child);
		}

		return allCardImages;
	}

}
