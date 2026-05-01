package application;

import application.backend.Card;
import application.backend.SpacedRepetitionService;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class StudyView {
	
	public static Scene create(Stage stage, Scene mainScene, List<Card> cards) {
		
		// track current card
		final int[] index = {0};
		
		// Question Scene
		VBox questionLayout = new VBox(30);  // creates a vertical box with padding
		questionLayout.setPadding(new Insets(40));
		
		Text questionText = new Text(); // text initialization
		questionText.setFont(Font.font(30));
		
		Button doneBtn = new Button("DONE");
		// adding text and button to to vbox named question layout
		questionLayout.getChildren().addAll(questionText, doneBtn);
		
		// loading question Scene vbox into a new scene
        Scene questionScene = new Scene(questionLayout, 900, 640); 
		
        // --- --- -- -- -  Answer Scene - -- -- --- ---
        VBox answerLayout = new VBox(30);
        answerLayout.setPadding(new Insets(40)); // adding padding to vbox
        
        Text answerText = new Text();
        answerText.setFont(Font.font(30)); 
        
        // Rating Buttons
        Button againBtn = new Button("0 - Failed");
        Button hardBtn = new Button("1 - Hard");
        Button medBtn = new Button("2 - Med");
        Button easyBtn = new Button("3 - Easy");
        Button perfectBtn = new Button("4 - Perfect");
        
        Button exitBtn = new Button("Exit");
        
        answerLayout.getChildren().addAll( // adding stuff to ans layout
        		answerText,
        		againBtn, hardBtn, medBtn, easyBtn, perfectBtn,
        		exitBtn
        		);
        
        Scene answerScene = new Scene(answerLayout, 900, 640); // ans Scene
        
        // Function to load card
        Runnable loadCard = () -> {
        	
        	if (index[0] >= cards.size()) { // if cards are more than zero then load mainscene
        		stage.setScene(mainScene);
        		return;
        	}
        	
        	Card current = cards.get(index[0]);
        	
        	questionText.setText(current.question); // gets q and a using index of card
        	answerText.setText(current.answer);
        };
	
	// --------- Navigation ------------
    doneBtn.setOnAction(e -> {
    	stage.setScene(answerScene);
    });
	
	// Rating buttons 
    againBtn.setOnAction(e -> handleAnswer(cards, index, 0, loadCard, stage, mainScene, questionScene));
    hardBtn.setOnAction(e -> handleAnswer(cards, index, 1, loadCard, stage, mainScene, questionScene));
    medBtn.setOnAction(e -> handleAnswer(cards, index, 2, loadCard, stage, mainScene, questionScene));
    easyBtn.setOnAction(e -> handleAnswer(cards, index, 3, loadCard, stage, mainScene, questionScene));
    perfectBtn.setOnAction(e -> handleAnswer(cards, index, 4, loadCard, stage, mainScene, questionScene));
    
    
    // exit n save n go back
    exitBtn.setOnAction( e -> stage.setScene(mainScene));
    
    // Start
    loadCard.run(); // runs loadcard opps
    return questionScene; // returns question scene, from there ans scene
	}
	
	// handle ans
	private static void handleAnswer(List<Card> cards,
									 int[] index, // has use here itself look above
									 int score,
									 Runnable loadCard,
									 Stage stage,
									 Scene mainScene, // used to send back to mainScene
									 Scene questionScene) {
		
		// define current
		Card current = cards.get(index[0]);
		
		// update spaced repetition
		SpacedRepetitionService.updateCard(current, score);
		
		// move to next card
		index[0]++;
		
		// handles logic if last card tells it to exit
		if (index[0] >= cards.size()) {
			stage.setScene(stage.getScene());
		}
		
		loadCard.run(); // starts run
		stage.setScene(questionScene); // sets the stage for question's scenef
	}
	
}