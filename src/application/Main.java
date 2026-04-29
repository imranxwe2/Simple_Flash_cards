package application;

import application.backend.CardDAO;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import application.backend.Card;
import application.backend.Deck;
import application.backend.DBInitializer;
import application.backend.Database;
import application.backend.DeckDAO;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
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
		
		
		
		
		// Experimental inserts deck A
		// DBInitializer.initialize();  
		// DeckDAO.insertDeck("Deck A");
		
		
		// Creating a deck with cards
		List <Card> cards = new ArrayList<>();
		
		try {
			cards.add(new Card(0,0, "Capital of france is ?","Paris",0,false));
			cards.add(new Card(0,0, "2+2?", "4",0,false));
			
			int deckId = DeckDAO.createDeckWithCards("General Knowledge", cards);
			System.out.println("Created deck with id ID: "+ deckId);

		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
		}
		
		/* inserting multiple cards into a deck 
		List<Card> newCards = new ArrayList<>();
		
		newCards.add(new Card(0,0,"Q1","A1",0,false));
		newCards.add(new Card(0,0,"Q2","A2",0,false));
		newCards.add(new Card(0,0,"Q3","A3",0,false));
		
		CardDAO.insertMultipleCardsByDeckName("General Knowledge", newCards);
		
		// get latest cards of x deck ( Alert needs further testing to see if only 3 cards are pulled )
		List<Card> preview = CardDAO.getPreviewCardsByDeckName("General Knowledge");
		VBox cardBox = new VBox(5);
		
		// debug
		System.out.println("Preview size: " + preview.size());
		for (Card c : preview) { // printing question
		    System.out.println("Question: " + c.question);
		} */
		

		
		
		/* delete deck
			DeckDAO.deleteDeckByName("General Knoledge");
			System.out.println("delete operation done"); */
		
		// UI we are going to use wrap and scrol plane
		List<Deck> decks = DeckDAO.getAllDecks();
		
		VBox container = new VBox(20);
		container.setPadding(new Insets(20));
		
		// we are using a scene wrapper we can assign it later but still reference it for our use here
		final Scene[] mainSceneHolder = new Scene[1];
		
		for (Deck d: decks) {
			
			
			
			
			VBox deckCard = new VBox(10);
			deckCard.setPrefSize(180, 220);
			deckCard.setPadding(new Insets(10));
			
			deckCard.setStyle(
				    "-fx-background-color: #d9d9d9;" +
				    "-fx-background-radius: 10;"
				);
				
			// deck title
			HBox header = new HBox(); // horizontal container
			header.setSpacing(10);
			
			Text title = new Text(d.name);
			title.setFont(Font.font(16));
			
			Region spacer = new Region();
			HBox.setHgrow(spacer, Priority.ALWAYS);
			
			Button editBtn = new Button("↗");
			
			// button action (for edit deck button)
			editBtn.setOnAction(e ->{
			    Scene editScene = EditDeckView.create(stage, mainSceneHolder[0], d);
			    stage.setScene(editScene);
			});
			
			editBtn.setStyle(
				    "-fx-background-color: transparent;" +
				    "-fx-font-size: 14;"
			);
			
			header.getChildren().addAll(title, spacer, editBtn);
			deckCard.getChildren().add(header);
			
			
			// get preview cards here
			List<Card> preview = CardDAO.getPreviewCardsByDeckName(d.name);
			
			for (Card c: preview) {
				Label q = new Label(c.question);
				
				q.setStyle(
			         "-fx-background-color: #cfcfcf;" +
			         "-fx-padding: 5;" +
			         "-fx-background-radius: 5;"
						);
				deckCard.getChildren().add(q);
				
			}
			container.getChildren().add(deckCard);
			
			// Button
			Button studyBtn = new Button("Study Now");
			deckCard.getChildren().add(studyBtn);
			

		}
		
		ScrollPane scrollPane = new ScrollPane(container); // Wrap in scroll plane
		scrollPane.setFitToWidth(true);
		
		
		
		Scene mainScene = new Scene(scrollPane, 900, 640);
		mainSceneHolder[0] = mainScene; // alias for mainScene so it can be used before ini
		
		stage.setScene(mainScene);
		stage.show();
		
		// creating a card object and inserting a card using card file, and cardao
		// Card card = new Card(0,1, "This is the question", "this is the answer",2,false);
		// CardDAO.insertCard(card);
		// we need to pass root node, these root nodes are mangers which set rules for components behave (buttons,textbox)
		Image icon = new Image("icon.png");
		
		stage.getIcons().add(icon);
		stage.setTitle("Simple Flash Cards");
		stage.setWidth(900);
		stage.setHeight(640);
		stage.setResizable(true);
		// stage.setFullScreen(true);
		
		// logic of connecting the + button with ui and it's dedicated function
		Button addDeckBtn = new Button("+");
		container.getChildren().add(0, addDeckBtn);
		addDeckBtn.setOnAction(e -> {
			Scene addScene = createAddDeckScene(stage, mainScene);
			stage.setScene(addScene);
		});
		
		
		// heading text
		Text title = new Text("My decks");
		title.setFont(Font.font("inter", 55));
		container.getChildren().add(0, title);
	}
	
	

	
	private Scene createAddDeckScene(Stage stage, Scene mainScene) {
		// this method creates a new screen for addition of decks
		// Ui layout
		VBox layout = new VBox(15);
		layout.setPadding(new Insets(30));
	
		// Deck Name field
		Label deckLabel = new Label("Deck Name");
		TextField deckNameField = new TextField(); // creates a input text field in ui
		
		// Question field
		Label qlabel = new Label("Enter Question ?");
		TextField questionField = new TextField();
		
		// Answer field
		Label alabel = new Label("Enter Answer");
		TextField answerField = new TextField();
		
		// Buttons
		Button addBtn = new Button("Add");
		Button finishBtn = new Button("Finish");
		Button cancelBtn = new Button("Cancel");
	
		// store cards temporarily, when you click save they move from memory to storage
		List<Card> tempCards = new ArrayList<>();
		
		// Add button's logic
		addBtn.setOnAction(e -> {
			String q = questionField.getText();
			String a = answerField.getText();
			
			
			if (!q.isEmpty() && !a.isEmpty()) {
				tempCards.add(new Card(0,0,q,a,0,false));
				
				questionField.clear();
				answerField.clear();
				
				System.out.println("Added card");
			}
		});
		
		// Finish button
		finishBtn.setOnAction(e -> {
			String deckName = deckNameField.getText();
			
			if (deckName.isEmpty()) {
				System.out.println("Deck name is required !");
				return;
			}
			
			try {
				DeckDAO.createDeckWithCards(deckName, tempCards);
				
				// go back to main screen
				stage.setScene(mainScene);
				
			} catch (RuntimeException ex) {
				System.out.println(ex.getMessage());
			}
		});
	
		// Cancel Button
		cancelBtn.setOnAction(e -> stage.setScene(mainScene));
		
		// Add everything to layout
		layout.getChildren().addAll(
				cancelBtn,
				deckLabel, deckNameField,
				qlabel, questionField,
				alabel, answerField,
				addBtn,
				finishBtn
				);
		
		// Return Scene
		return new Scene(layout, 900,640);
	
	}
}

