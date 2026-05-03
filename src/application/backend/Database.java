package application.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:sfc_db.db";

    // Connect to SQLite
    public static Connection connect() {
        try {
            // 🔍 VERY IMPORTANT (helps you find DB file)
            System.out.println("DB PATH: " + System.getProperty("user.dir"));

            Connection conn = DriverManager.getConnection(URL);

            // Enable foreign keys
            Statement stmt = conn.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON");

            System.out.println("Connected to SQLite");
            return conn;

        } catch (SQLException e) {
            System.out.println("Connection Failed: " + e.getMessage());
            return null;
        }
    }

    // Initialize database tables
    public static void initializeDatabase() {

        String createDecksTable =
                "CREATE TABLE IF NOT EXISTS decks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL" +
                ");";

        String createCardsTable =
                "CREATE TABLE IF NOT EXISTS cards (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "deck_id INTEGER, " +
                "question TEXT, " +
                "answer TEXT, " +
                "score INTEGER, " +
                "completed BOOLEAN, " +
                "next_review INTEGER, " +   // ✅ FIXED
                "FOREIGN KEY(deck_id) REFERENCES decks(id)" +
                ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createDecksTable);
            stmt.execute(createCardsTable);

            System.out.println("Tables created successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}