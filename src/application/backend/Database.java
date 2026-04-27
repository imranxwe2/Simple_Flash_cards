// Handles connection with Sqlite (database engine), using JDBC driver.
package application.backend;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class Database {
	
	private static final String URL = "jdbc:sqlite:src/database/sfc_db"; // path is experimental
// we are defining how connection lib can access our database
	public static Connection connect() {
		try {
			Connection conn = DriverManager.getConnection(URL);
			
			// enabling foreign keys here
			Statement stmt = conn.createStatement();
			stmt.execute("PRAGMA foreign_keys = ON");
			
			return conn;
			
			
		}	catch (SQLException e) {
				System.out.println("Connection Failed"+ e.getMessage());
				return null;
		}
	}
}