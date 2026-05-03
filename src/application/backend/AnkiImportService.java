package application.backend;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AnkiImportService {

    // 🔥 MAIN ENTRY POINT
    public static void importApkg(File apkgFile, String deckName) {

        try {
            File tempDir = unzipApkg(apkgFile);

            File dbFile = new File(tempDir, "collection.anki2");

            if (!dbFile.exists()) {
                System.out.println("collection.anki2 not found!");
                return;
            }

            List<Card> cards = extractCardsFromDB(dbFile);

            if (cards.isEmpty()) {
                System.out.println("No cards found in deck");
                return;
            }

            DeckDAO.createDeckWithCards(deckName, cards);

            // cleanup temp folder
            deleteDirectory(tempDir);

            System.out.println("Imported " + cards.size() + " cards");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 STEP 1: UNZIP
    private static File unzipApkg(File apkgFile) throws IOException {

        File tempDir = Files.createTempDirectory("apkg_import").toFile();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(apkgFile))) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                File newFile = new File(tempDir, entry.getName());

                try (FileOutputStream fos = new FileOutputStream(newFile)) {

                    byte[] buffer = new byte[1024];
                    int len;

                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
        }

        return tempDir;
    }

    // 🔥 STEP 2: READ SQLITE
    private static List<Card> extractCardsFromDB(File dbFile) {

        List<Card> cards = new ArrayList<>();

        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT flds FROM notes");

            while (rs.next()) {

                String flds = rs.getString("flds");

                // 🔥 split fields (Anki separator)
                String[] parts = flds.split("\u001F");

                if (parts.length >= 2) {

                    String question = cleanHTML(parts[0]);
                    String answer = cleanHTML(parts[1]);

                    cards.add(new Card(
                            0,
                            0,
                            question,
                            answer,
                            0,
                            false,
                            null
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cards;
    }

    // 🔥 STEP 3: CLEAN HTML
    private static String cleanHTML(String input) {

        if (input == null) return "";

        // remove HTML tags
        String cleaned = input.replaceAll("<[^>]*>", "");

        // basic entity replacements
        cleaned = cleaned.replace("&nbsp;", " ");
        cleaned = cleaned.replace("&amp;", "&");
        cleaned = cleaned.replace("&lt;", "<");
        cleaned = cleaned.replace("&gt;", ">");

        return cleaned.trim();
    }

    // 🔥 STEP 4: CLEANUP TEMP
    private static void deleteDirectory(File dir) {

        if (dir.isDirectory()) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                deleteDirectory(file);
            }
        }

        dir.delete();
    }
}