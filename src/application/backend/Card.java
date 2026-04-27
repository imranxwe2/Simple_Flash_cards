// Simply creates card


// Deck and Card objects are used in Java to hold data, 
// which is then passed to SQLite using DAO classes
package application.backend;

public class Card {
	// defining variables
	public int id;
	public int deckId;
	public String question;
	public String answer;
	public int score;
	public boolean completed; // completed can be yes or no '1/0'
	
	public Card(int id, int deckId, String question, String answer, int score, boolean completed) {
	// this part of code is used to pass a value into a variable "assignment"
	// which is then later sent to sqlite
		this.id = id; // L Object variable, R value passed into it
		this.deckId = deckId;
		this.question = question;
		this.answer = answer;
		this.score = score;
		this.completed = completed;	
	}	
	
	
}