package application;

import application.backend.Card;
import application.backend.DeckDAO;
import application.backend.AIService;
import application.backend.SettingsService;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class AIView {

    public static Scene create(Stage stage, Scene mainScene) {

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));

        Label topicLabel = new Label("Enter Topic:");
        TextField topicField = new TextField();

        Label amountLabel = new Label("Number of cards:");
        TextField amountField = new TextField();

        Button generateBtn = new Button("Generate");
        Button backBtn = new Button("Back");
        Button changeKeyBtn = new Button("Change API Key");
        
        // warning
        Label warning = new Label("⚠ Only compatible with Google Gemini API keys (AIza...)");
        warning.setStyle(
            "-fx-text-fill: #cc0000;" +   // red color
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;"
        );
        
        // 🔙 Back button
        backBtn.setOnAction(e -> stage.setScene(mainScene));

        // 🔥 Generate cards
        generateBtn.setOnAction(e -> {

            String topic = topicField.getText().trim();

            if (topic.isEmpty()) {
                showError("Please enter a topic.");
                return;
            }

            int amount;

            try {
                amount = Integer.parseInt(amountField.getText());

                if (amount <= 0 || amount > 20) {
                    showError("Enter a number between 1 and 20.");
                    return;
                }

            } catch (Exception ex) {
                showError("Invalid number of cards.");
                return;
            }

            try {
                List<Card> cards = AIService.generateCards(topic, amount);

                if (cards == null || cards.isEmpty()) {
                    showError("AI failed. Check API key or quota.");
                    return;
                }

                // ask deck name
                TextInputDialog dialog = new TextInputDialog(topic + " Deck");
                dialog.setTitle("Deck Name");
                dialog.setHeaderText("Enter name for generated deck:");

                Optional<String> result = dialog.showAndWait();

                if (!result.isPresent()) return;

                String deckName = result.get().trim();

                if (deckName.isEmpty()) {
                    showError("Deck name cannot be empty.");
                    return;
                }

                DeckDAO.createDeckWithCards(deckName, cards);

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText("Deck Created");
                success.setContentText("AI generated deck successfully!");
                success.showAndWait();

                stage.setScene(mainScene);

            } catch (Exception ex) {

                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("AI Generation Failed");
                error.setContentText(ex.getMessage());
                error.showAndWait();
            }
        });

        // 🔐 Change API Key
        changeKeyBtn.setOnAction(e -> {

            String currentKey = SettingsService.getApiKey();

            TextInputDialog dialog = new TextInputDialog(currentKey);
            dialog.setTitle("Change API Key");
            dialog.setHeaderText("Enter your OpenAI API key:");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                String newKey = result.get().trim();

                if (!newKey.isEmpty()) {
                    SettingsService.saveApiKey(newKey);

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setHeaderText("API Key Updated");
                    success.setContentText("New key saved successfully.");
                    success.showAndWait();
                }
            }
        });

        // layout
        layout.getChildren().addAll(
                backBtn, warning, 
                topicLabel, topicField,
                amountLabel, amountField,
                generateBtn,
                changeKeyBtn
        );

        return new Scene(layout, 900, 640);
    }

    // 🔥 helper method
    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}