// Simply holds the deck objects on java side does not handle communication
// blue print to create object

package application.backend;

public class Deck {
	public int id;
	public String name;
	
	public Deck(int id, String name) {
		// this part of code is used to pass a value into a variable "assignment"
		// which is then later sent to sqlite
		this.id = id; // assigns value
		this.name = name;  // L object variable, R value passed in
	}
	
	// example passing values Deck d = new Deck(1,"Dec A");
}