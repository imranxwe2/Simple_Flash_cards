// DeckDAO is responsible for saving decks into Sqlite
package application.backend;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeckDAO {
	
	
	public static void insertDeck(String name) {

	    String sql = "INSERT INTO decks(name) VALUES(?)";

	    try (Connection conn = Database.connect();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setString(1, name);
	        pstmt.executeUpdate();

	    } catch (SQLException e) {

	        if (e.getMessage().contains("UNIQUE")) {
	            throw new RuntimeException("Deck already exists!");
	        }

	        throw new RuntimeException(e);
	    }
	}
	
	// delete deck Note this function is ai generated
	public static void deleteDeckByName(String name) {

	    String sql = "DELETE FROM decks WHERE name = ?";

	    try (Connection conn = Database.connect();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setString(1, name);
	        pstmt.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	
	// Creating Deck with Cards
	public static int createDeckWithCards(String deckName, List<Card> cards) {

	    int deckId = -1;
	    String sql = "INSERT INTO decks(name) VALUES(?)";

	    try (Connection conn = Database.connect();
	         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        pstmt.setString(1, deckName);
	        pstmt.executeUpdate();

	        ResultSet rs = pstmt.getGeneratedKeys();
	        if (rs.next()) {
	            deckId = rs.getInt(1);
	        }

	        for (Card c : cards) {
	            c.deckId = deckId;
	            CardDAO.insertCard(c);
	        }

	    } catch (SQLException e) {

	        if (e.getMessage().contains("UNIQUE")) {
	            throw new RuntimeException("Deck already exists!");
	        }

	        throw new RuntimeException(e);
	    }

	    return deckId;
	}
	
	
	
	
	// get all decks
	public static List<Deck> getAllDecks() {
		
		List<Deck> decks = new ArrayList<>();
		String sql = "SELECT * FROM decks";
		
		try (Connection conn = Database.connect();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			
			while (rs.next()) {
				decks.add(new Deck(
						rs.getInt("id"),
						rs.getString("name")
						));
				}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return decks;
	}
}