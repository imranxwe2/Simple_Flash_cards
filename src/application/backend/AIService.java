package application.backend;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AIService {

    public static List<Card> generateCards(String topic, int amount) {

        List<Card> cards = new ArrayList<>();

        try {
            String apiKey = SettingsService.getApiKey();

            if (apiKey == null || apiKey.isEmpty()) {
                throw new RuntimeException("API key not set");
            }

            // ✅ Correct Gemini endpoint
            URL url = new URL(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent"
            );

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            // ✅ Correct authentication
            conn.setRequestProperty("X-goog-api-key", apiKey);

            conn.setDoOutput(true);

            // 🔥 Better prompt (clean output)
            String prompt =
                    "Generate exactly " + amount + " flashcards about " + topic + ".\n" +
                    "Only output lines in this exact format:\n" +
                    "Question | Answer\n" +
                    "No numbering. No extra text.";

            // 🔐 Safe JSON escaping
            String safePrompt = prompt
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n");

            String jsonInput =
                    "{"
                    + "\"contents\": ["
                    + "  {"
                    + "    \"parts\": ["
                    + "      {\"text\": \"" + safePrompt + "\"}"
                    + "    ]"
                    + "  }"
                    + "]"
                    + "}";

            // 📤 Send request
            OutputStream os = conn.getOutputStream();
            os.write(jsonInput.getBytes("utf-8"));
            os.close();

            // 🔥 Handle success + error properly
            int status = conn.getResponseCode();

            InputStream is = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            br.close();

            System.out.println("STATUS: " + status);
            System.out.println("RESPONSE: " + response);

            if (status < 200 || status >= 300) {
                throw new RuntimeException("API Error: " + response.toString());
            }

            // 🔥 Extract text safely
            String content = extractContent(response.toString());

            // 🔥 Convert to cards
            cards = parseCards(content);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cards;
    }

    // ✅ Robust extraction (no fragile substring bug)
    private static String extractContent(String json) {

        try {
            String key = "\"text\": \"";
            int start = json.indexOf(key);

            if (start == -1) return "";

            start += key.length();

            StringBuilder result = new StringBuilder();

            for (int i = start; i < json.length(); i++) {
                char c = json.charAt(i);

                if (c == '"' && json.charAt(i - 1) != '\\') {
                    break;
                }

                result.append(c);
            }

            return result.toString()
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"");

        } catch (Exception e) {
            return "";
        }
    }

    // ✅ Clean parsing
    private static List<Card> parseCards(String text) {

        List<Card> cards = new ArrayList<>();

        String[] lines = text.split("\n");

        for (String line : lines) {

            if (line.contains("|")) {

                String[] parts = line.split("\\|");

                if (parts.length >= 2) {

                    String q = parts[0].trim();
                    String a = parts[1].trim();

                    if (!q.isEmpty() && !a.isEmpty()) {
                        cards.add(new Card(0, 0, q, a, 0, false, null));
                    }
                }
            }
        }

        return cards;
    }
}