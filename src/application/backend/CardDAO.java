package application.backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardDAO {
	
	// get next review time of deck
	public static String getNextReveiwTime(int deckId) {
		 
		String sql = 
				"SELECT MIN(next_review) AS next_time " +
				"FROM cards WHERE deck_id = ? AND next_review IS NOT NULL";
				
		try (Connection conn = Database.connect();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setInt(1, deckId);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				return rs.getString("next_time");
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	// checks if deck	 is compelted
	public static boolean isDeckCompleted(int deckId) {
		
		String sql = "SELECT COUNT(*) AS remaining FROM cards WHERE deck_id = ? AND completed = 0";
		
		try (Connection conn = Database.connect();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			
			pstmt.setInt(1, deckId);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next() ) {
				return rs.getInt("remaining") == 0; // returns zero cards remaining
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false; // false still needs to be completed
	}

    // 🔥 Update spaced repetition data
    public static void updateReviewData(int id, int score, boolean completed, String nextReview) {

        String sql = "UPDATE cards SET score = ?, completed = ?, next_review = ? WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, score);
            pstmt.setInt(2, completed ? 1 : 0);
            pstmt.setString(3, nextReview);
            pstmt.setInt(4, id);

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 Insert single card by deck name
    public static void insertCardByDeckName(String deckName, String q, String a) {

        String sql =
            "INSERT INTO cards(deck_id, question, answer, score, completed) " +
            "SELECT id, ?, ?, 0, 0 FROM decks WHERE name = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, q);
            pstmt.setString(2, a);
            pstmt.setString(3, deckName);

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 Delete card
    public static void deleteCard(int id) {

        String sql = "DELETE FROM cards WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 Update card text
    public static void updateCard(int id, String q, String a) {

        String sql = "UPDATE cards SET question = ?, answer = ? WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, q);
            pstmt.setString(2, a);
            pstmt.setInt(3, id);

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 Batch insert cards
    public static void insertMultipleCardsByDeckName(String deckName, List<Card> cards) {

        String sql =
            "INSERT INTO cards(deck_id, question, answer, score, completed) " +
            "SELECT id, ?, ?, 0, 0 FROM decks WHERE name = ?";

        try (Connection conn = Database.connect()) {

            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                for (Card c : cards) {
                    pstmt.setString(1, c.question);
                    pstmt.setString(2, c.answer);
                    pstmt.setString(3, deckName);
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 Preview (LIMIT 3)
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
                    rs.getInt("completed") == 1,
                    rs.getString("next_review")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cards;
    }

    // 🔥 Insert card (direct)
    public static void insertCard(Card card) {

        String sql =
            "INSERT INTO cards(deck_id, question, answer, score, completed, next_review) VALUES(?,?,?,?,?,?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, card.deckId);
            pstmt.setString(2, card.question);
            pstmt.setString(3, card.answer);
            pstmt.setInt(4, card.score);
            pstmt.setInt(5, card.completed ? 1 : 0);
            pstmt.setString(6, card.nextReview);

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 Get all cards in a deck
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
                    rs.getInt("deck_id"),
                    rs.getString("question"),
                    rs.getString("answer"),
                    rs.getInt("score"),
                    rs.getInt("completed") == 1,
                    rs.getString("next_review")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cards;
    }

    // 🔥 Mark completed
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