package server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author Philipp Lehmann
 *
 */
public class DBConnect {
	String databaseURL = "jdbc:mysql://localhost:3306/dominiondb?useSSL=false";
	String user = "dominion";
	String password = "dominion";
	Connection conn = null;

	/**
	 * opens DB connection
	 * 
	 * @author Philipp Lehmann
	 *
	 */
	public DBConnect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(databaseURL, user, password);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * closes DB connection
	 * 
	 * @author Philipp Lehmann
	 *
	 */
	public void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
}