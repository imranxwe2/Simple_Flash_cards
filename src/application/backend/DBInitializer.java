// Creates and manages decks and cards inside sql by using table

package application.backend;

import java.sql.Connection;
import java.sql.Statement;

public class DBInitializer {
	
	public static void initialize() {
		//decks table is a table in sqlite that holds all your decks
		
		
		String decksTable = 
				"CREATE TABLE IF NOT EXISTS decks (" // create table if decks does not exist
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " // prim unique given to all item in column (the rows) + autoincremented id2 = id1 + on 1 and so
				+ "name TEXT NOT NULL" // column name that stores deck name should not be null, note don't use traling commas at the end
				+ ");";
			
		
	
	// TLDR: cards_table is a child of deck_table
	
		String cardsTable =
				"CREATE TABLE IF NOT EXISTS cards ("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "deck_id INTEGER,"
				+ "question TEXT,"
				+ "answer TEXT,"
				// we we specific completed then completed is = 1 else the value of completed stays 0 if not specified
				+ "completed INTEGER DEFAULT 0," 
				+ "next_review Text," // next time when the card should be reviewed
				// fore.. key links with prim key
				+ "FOREIGN KEY(deck_id) REFERENCES decks(id));";
				
	
		// JAVA DATABASE INTERACTION
		try (Connection conn = Database.connect();
			 Statement stmt = conn.createStatement()) { // using statement from jdbc to create a statement
			
			stmt.execute(decksTable); // starts deckstable execution statement is just a tool for communicating with sqlite
			stmt.execute(cardsTable); // stmt is used to invoke javacode into sql
			
			System.out.println("Tabels Created");
		} catch (Exception e) {
			e.printStackTrace(); // pST used get debug info on terminal 
		}
	}
}
			
		
	

		