package application;

import application.backend.Card;
import application.backend.CardDAO;
import application.backend.Deck;
import application.backend.DeckDAO;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class EditDeckView {

    public static Scene create(Stage stage, Scene mainScene, Deck d) {

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Text heading = new Text("Editing: " + d.name);
        heading.setFont(Font.font(20));

        VBox cardContainer = new VBox(10);

        // load cards
        List<Card> cards = CardDAO.getCardsByDeck(d.id);

        for (Card c : cards) {

            VBox cardBox = new VBox(8);
            cardBox.setPadding(new Insets(10));
            cardBox.setStyle("-fx-border-color: #ccc;");

            Label qLabel = new Label("Question");
            TextField qField = new TextField(c.question);

            Label aLabel = new Label("Answer");
            TextField aField = new TextField(c.answer);

            Button deleteBtn = new Button("Delete");

            deleteBtn.setOnAction(e -> {
                cardContainer.getChildren().remove(cardBox);
                CardDAO.deleteCard(c.id);
            });

            cardBox.getChildren().addAll(qLabel, qField, aLabel, aField, deleteBtn);

            cardBox.setUserData(c.id);

            cardContainer.getChildren().add(cardBox);
        }

        // Add new card
        Button addBtn = new Button("Add New Card");

        addBtn.setOnAction(e -> {

            VBox newCard = new VBox(8);
            newCard.setPadding(new Insets(10));
            newCard.setStyle("-fx-border-color: #ccc;");

            TextField qField = new TextField();
            TextField aField = new TextField();

            Button deleteBtn = new Button("Delete");

            deleteBtn.setOnAction(ev -> cardContainer.getChildren().remove(newCard));

            newCard.getChildren().addAll(
                    new Label("Question"), qField,
                    new Label("Answer"), aField,
                    deleteBtn
            );

            newCard.setUserData(null);

            cardContainer.getChildren().add(newCard);
        });

        // Save
        Button saveBtn = new Button("Finish");

        saveBtn.setOnAction(e -> {

            for (Node node : cardContainer.getChildren()) {

                VBox cardBox = (VBox) node;

                TextField qField = (TextField) cardBox.getChildren().get(1);
                TextField aField = (TextField) cardBox.getChildren().get(3);

                Object idObj = cardBox.getUserData();

                if (idObj != null) {

                    int cardId = (int) idObj;

                    CardDAO.updateCard(
                            cardId,
                            qField.getText(),
                            aField.getText()
                    );

                } else {

                    CardDAO.insertCardByDeckName(
                            d.name,
                            qField.getText(),
                            aField.getText()
                    );
                }
            }

            stage.setScene(mainScene);
        });

        // Delete deck
        Button deleteDeckBtn = new Button("Delete Deck");
        deleteDeckBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");

        deleteDeckBtn.setOnAction(e -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Deck");
            alert.setHeaderText("⚠ Are you sure?");
            alert.setContentText("This will delete ALL cards.");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {

                DeckDAO.deleteDeckAndCards(d.id);
                stage.setScene(mainScene);
            }
        });

        // Back
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> stage.setScene(mainScene));

        ScrollPane scrollPane = new ScrollPane(cardContainer);
        scrollPane.setFitToWidth(true);

        layout.getChildren().addAll(
                backBtn,
                heading,
                scrollPane,
                addBtn,
                saveBtn,
                deleteDeckBtn
        );

        return new Scene(layout, 900, 640);
    }
}