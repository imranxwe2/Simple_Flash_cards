package application;

import javafx.stage.FileChooser;
import java.io.File;
import java.util.Optional;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import application.backend.AnkiImportService;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.net.URI;

public class ImportView {

    public static Scene create(Stage stage, Scene mainScene) {

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));

        // Title
        Text heading = new Text("Import Decks");

        // Small info text
        Text info = new Text("Download decks from:");
        info.setStyle("-fx-font-size: 12px; -fx-fill: #555;");

        // Clickable link
        Text link = new Text("https://ankiweb.net/shared/decks");
        link.setStyle("-fx-fill: blue; -fx-underline: true;");

        link.setOnMouseClicked(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://ankiweb.net/shared/decks"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Back button
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> stage.setScene(mainScene));


        
        Button chooseFileBtn = new Button("Import .apkg File");
        
        chooseFileBtn.setOnAction(e -> {
        	
        	FileChooser fileChooser = new FileChooser();
        	fileChooser.setTitle("Open Anki deck !");
        	
        	fileChooser.getExtensionFilters().add(
        			new FileChooser.ExtensionFilter("Anki Deck (*.apkg)", "*.apkg")
        			);
        
        	File file = fileChooser.showOpenDialog(stage);
        	
        	if (file != null) {
        		
        		// Ask for deck name
        		TextInputDialog dialog = new TextInputDialog("Imported Deck");
        		dialog.setTitle("Deck Name");
        		dialog.setHeaderText("Enter name for imported deck");   
        		
        		Optional<String> result = dialog.showAndWait();
        		
        		if (!result.isPresent()) return;
        		
        		String deckName = result.get();
        		
        		try {
        			AnkiImportService.importApkg(file, deckName);
        			
        			// Success popup
        			Alert success = new Alert(Alert.AlertType.INFORMATION);
        			success.setTitle("Success");
                    success.setHeaderText("Import Complete");
                    success.setContentText("Deck imported successfully!");
                    success.showAndWait();

                    stage.setScene(mainScene);
        		
        		} catch (Exception ex) {
        			Alert error = new Alert(Alert.AlertType.ERROR);
        			error.setTitle("Error");
        			error.setHeaderText("Import Failed");
        			error.setContentText(ex.getMessage());
        			error.showAndWait();
        		}
        		}
        });

        layout.getChildren().addAll(
                backBtn,
                heading,
                info,
                link,
                chooseFileBtn
        );
        
        return new Scene(layout, 900, 640);
       
    }
    
    
}