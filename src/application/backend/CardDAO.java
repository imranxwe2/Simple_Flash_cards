package application.backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardDAO {
	
	// Insert a card into a deck using it's name
	public static void insertMultipleCardsByDeckName(String deckName, List<Card> cards) {

		String sql =
			    "INSERT INTO cards(deck_id, question, answer, score, completed) " +
			    "SELECT id, ?, ?, 0, 0 " +   // ← space after 0
			    "FROM decks " +
			    "WHERE name = ?";	

	    try (Connection conn = Database.connect()) {

	        conn.setAutoCommit(false); // ✅ start transaction

	        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

	            for (Card c : cards) {
	                pstmt.setString(1, c.question);
	                pstmt.setString(2, c.answer);
	                pstmt.setString(3, deckName);
	                pstmt.addBatch(); // ✅ queue insert
	            }

	            pstmt.executeBatch(); // ✅ run all inserts together
	            conn.commit();        // ✅ save everything

	        } catch (Exception e) {
	            conn.rollback();      // ❌ roll back if anything fails
	            throw e;
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	// Preview card questions 
	public static List<Card> getPreviewCardsByDeckName(String deckName) {
		
		List<Card> cards = new ArrayList<>();
		
		String sql =
			    "SELECT c.* " +
			    "FROM cards c " +
			    "JOIN decks d ON c.deck_id = d.id " +
			    "WHERE d.name = ? " +
			    "ORDER BY c.completed ASC, c.score ASC " +
			    "LIMIT 3";
		
		try (Connection conn = Database.connect();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setString(1, deckName);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				cards.add(new Card(
						rs.getInt("id"),
						rs.getInt("deck_id"),
						rs.getString("question"),
						rs.getString("answer"),
						rs.getInt("score"),
						rs.getInt("completed") == 1
					));
			}
		} catch	(Exception e) {
			e.printStackTrace();
		}
		return cards;				
	}
	
	
    // ✅ Insert card
    public static void insertCard(Card card) {

        String sql = "INSERT INTO cards(deck_id, question, answer, score, completed) VALUES(?,?,?,?,?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, card.deckId);
            pstmt.setString(2, card.question);
            pstmt.setString(3, card.answer);
            pstmt.setInt(4, card.score); // ✅ FIXED
            pstmt.setInt(5, card.completed ? 1 : 0); // ✅ FIXED

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    

    // ✅ Get cards by deck (shows all specific cards that belong to a deck)
    public static List<Card> getCardsByDeck(int deckId) {

        List<Card> cards = new ArrayList<>();
        String sql = "SELECT * FROM cards WHERE deck_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deckId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                cards.add(new Card(
                        rs.getInt("id"),
                        rs.getInt("deck_id"), // ✅ FIXED
                        rs.getString("question"),
                        rs.getString("answer"),
                        rs.getInt("score"),
                        rs.getInt("completed") == 1 // ✅ FIXED
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cards;
    }

    // using card id mark card as completed
    public static void markCompleted(int cardId) {

        String sql = "UPDATE cards SET completed = 1 WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cardId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

