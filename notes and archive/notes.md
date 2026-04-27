Javafx
* Stage -> the window
* Scene -> what's inside the window
* Group -> a box that holds ui elements (text, buttons,etc)


// moves to next row and checks boolean value, next row of card, repeat rs execution
cards.add
1st call → moves to Row 1 → returns true  
2nd call → moves to Row 2 → returns true  
3rd call → moves to Row 3 → returns true  
4th call → no more rows → returns false ❌

CardDao
get all cards for a specific deck
List<Cards>, list comes from java.util.List, List is a box that can hold many items,
Card is just calling Card class (a object blueprint)


deck is completed when all cards are competed 'positive' + "completed INTEGER DEFAULT 0," 

open your database folder in src fodler, sqlite3 sfc_db (to access database)

Eclipse magic tweak can be used by Ctrl + space


prepared statement
allows us to use prewritten  placehodlers such as 	VALUES(?)

sql doesn't support trailing commas, example of correct code
col1,
col2,
col3


you don't need to add path to call other files as they are in src folder

What is Statement ? (it is communication tool)
Statement sends SQL commands from Java to the database

Java (your code)
   ↓
JDBC (Connection, Statement)
   ↓
SQLite (database file .db)

Connection
comes from import.sql.connection (JDBC the module you installed = java database Connectivity)
connects you with sql server and communicate with database

jdbc:sqlite:database/sfc_db.db
Simple_Flash_cards/
   └── database/
         └── sfc_db.db
         
so database is used to point out the address of where sfc_db is located our real database
where data lives



A foreign key is a column in one table (the child table) that references the primary key another
table, it links both of them togeather and linking the two and ensuring valid relationships and
ensuring data integrity


A primary 	key is a column that uniquely identifies each row in a table, also a primary key
is unique it cannot repeat (it is a column)

prim key (column 1)
1 (uniq identifier 32i9)
3 (uniq identifier 32i9)
2 (uniq identifier 32i9)
12 (uniq identifier 32i9)
23 (uniq identifier 32i9)

KEY AUTOINCREMENT
the number increases automatically for each row
Row 1 → id = 1
Row 2 → id = 2
Row 3 → id = 3

name TEXT NOT NULL 
column name must not be null

make database and decide how you will store them, you just replace .add function with ui by linking them togeather, the input of .add(inputa,inputb) will be replaced by Ui.

a card is just a vbox a vertical box of information, with a graphic element in it's background.
you first need to create the backend and get back to it	

Hi there make the start screen ui first

Note: while adding javafx in build path, couldn't check if it was in class path, refer to javafx
playlist

make sure you have relative paths to avoid error when making a shipable program

Java fx gui
is a layered animation theather
the deepest layer 1: is there "stage" (Basically the window of the application)

next layer, is "scene" is used for graphical content like logos, background pop arts etc

next layer "Scene graph" it is a tree data structure to hold various nodes, these nodes can be all the different components
example buttons, text boxes,images, variable elements.

when you have ask chat gpt code, for something his code won't match your code, you shoud not copy paste
or feed him your code, using logic see his code and implement in your own
