package application;

import application.backend.Card;
import application.backend.CardDAO;
import application.backend.Database;
import application.backend.Deck;
import application.backend.DeckDAO;
import application.backend.SpacedRepetitionService;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.image.Image;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        Connection conn = Database.connect();

        if (conn != null) {
            System.out.println("Connected to Sqlite");
        }

        // Optional test deck
        List<Card> cards = new ArrayList<>();

        try {
            cards.add(new Card(0,0, "Capital of france is ?","Paris",0,false, null));
            cards.add(new Card(0,0, "2+2?", "4",0,false, null));

            DeckDAO.createDeckWithCards("General Knowledge", cards);

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        Image icon = new Image("icon.png");
        stage.getIcons().add(icon);

        stage.setTitle("Simple Flash Cards");
        stage.setWidth(900);
        stage.setHeight(640);
        stage.setResizable(true);

        stage.setScene(createMainScene(stage));
        stage.show();
    }

    // 🔥 MAIN SCREEN
    public Scene createMainScene(Stage stage) {

    	List<Deck> decks = DeckDAO.getAllDecks();

    	// sorts incomplete first, completed last
    	decks.sort((d1, d2) -> {
    	    boolean c1 = CardDAO.isDeckCompleted(d1.id);
    	    boolean c2 = CardDAO.isDeckCompleted(d2.id);

    	    return Boolean.compare(c1, c2);
    	});
    	
    	// vertical and horizontal containers
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));

        // Title
        Text title = new Text("My decks");
        title.setFont(Font.font(30));
        
        
        
        // Add deck button
        Button addDeckBtn = new Button("+");
        Button refreshBtn = new Button("⟳");
        Button importBtn = new Button("Import");
        Button aiBtn = new Button("Ai");
        
        // action for refersh
        refreshBtn.setOnAction(e -> {
            stage.setScene(createMainScene(stage));
        });
        
        
        // action for import
        importBtn.setOnAction(e -> {
            Scene importScene = ImportView.create(stage, createMainScene(stage));
            stage.setScene(importScene);
        });
                	
        addDeckBtn.setOnAction(e -> {
            Scene addScene = createAddDeckScene(stage);
            stage.setScene(addScene);
        });

        topBar.getChildren().addAll(addDeckBtn, refreshBtn, importBtn, aiBtn);
        container.getChildren().addAll(title, topBar);

        for (Deck d : decks) {

            VBox deckCard = new VBox(10);
            deckCard.setPadding(new Insets(10));
            deckCard.setStyle(
                "-fx-background-color: #d9d9d9;" +
                "-fx-background-radius: 10;"
            );

            // 🔥 HEADER (title + arrow)
            HBox header = new HBox();
            header.setSpacing(10);

            Text deckTitle = new Text(d.name);
            deckTitle.setFont(Font.font(16));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button editBtn = new Button("↗");

            editBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-font-size: 14;"
            );

            editBtn.setOnAction(e -> {
                Scene editScene = EditDeckView.create(stage, createMainScene(stage), d);
                stage.setScene(editScene);
            });

            header.getChildren().addAll(deckTitle, spacer, editBtn);
            deckCard.getChildren().add(header);

            // 🔥 PREVIEW CARDS
            List<Card> preview = CardDAO.getPreviewCardsByDeckName(d.name);

            for (Card c : preview) {
                Label q = new Label(c.question);

                q.setStyle(
                    "-fx-background-color: #cfcfcf;" +
                    "-fx-padding: 5;" +
                    "-fx-background-radius: 5;"
                );

                deckCard.getChildren().add(q);
            }
            
            // Completed Button
            boolean isCompleted = CardDAO.isDeckCompleted(d.id);

            if (isCompleted) {
            	Text completedText = new Text("COMPLETED");
            	
            	completedText.setStyle(
            			"-fx-fill: red;" +
            			"-fx-font-size: 14px;" +
            			"-fx-font-weight: bold;"
            			);
            	
            	completedText.setRotate(0);
            	deckCard.getChildren().add(completedText);
            
            	// Get next Review time
            	String nextReview = CardDAO.getNextReveiwTime(d.id);
            	String readable = FormatTimeRemaining.formatTimeRemaining(nextReview);
            	Text nextText = new Text("Next review: " + readable); // displays the above strings
            	
            	deckCard.getChildren().add(nextText); // what does this do i think add's to getchildren scene
            	
            }
            // 🔥 STUDY BUTTON
            Button studyBtn = new Button("Study Now");

            studyBtn.setStyle(
                "-fx-background-color: #5c7cfa;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 5 15 5 15;"
            );
            
            // connection study now button to studyview
            studyBtn.setOnAction( e -> {
            	
            	List<Card> studyCards =
            			SpacedRepetitionService.buildStudyQueue(d.id);
            	
            	Scene studyScene = StudyView.create(
            			stage,
            			createMainScene(stage),
            			studyCards
            			);
            	stage.setScene(studyScene);
            });
            
            if (!isCompleted) {
            	deckCard.getChildren().add(studyBtn);
            }
            container.getChildren().add(deckCard);
        }

        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);

        return new Scene(scrollPane, 900, 640);
    }

    // ➕ ADD DECK SCREEN
    private Scene createAddDeckScene(Stage stage) {

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));

        Label deckLabel = new Label("Deck Name");
        TextField deckNameField = new TextField();

        Label qLabel = new Label("Enter Question ?");
        TextField questionField = new TextField();

        Label aLabel = new Label("Enter Answer");
        TextField answerField = new TextField();

        Button addBtn = new Button("Add");
        Button finishBtn = new Button("Finish");
        Button cancelBtn = new Button("Cancel");

        List<Card> tempCards = new ArrayList<>();

        addBtn.setOnAction(e -> {
            String q = questionField.getText();
            String a = answerField.getText();

            if (!q.isEmpty() && !a.isEmpty()) {
                tempCards.add(new Card(0, 0, q, a, 0, false, null));
                questionField.clear();
                answerField.clear();
            }
        });

        finishBtn.setOnAction(e -> {

            String deckName = deckNameField.getText();

            if (deckName.isEmpty()) {
                System.out.println("Deck name is required!");
                return;
            }

            try {
                DeckDAO.createDeckWithCards(deckName, tempCards);

                // 🔥 refresh main screen
                stage.setScene(createMainScene(stage));

            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
            }
        });

        cancelBtn.setOnAction(e -> stage.setScene(createMainScene(stage)));

        layout.getChildren().addAll(
                cancelBtn,
                deckLabel, deckNameField,
                qLabel, questionField,
                aLabel, answerField,
                addBtn,
                finishBtn
        );

        return new Scene(layout, 900, 640);
    }
}
