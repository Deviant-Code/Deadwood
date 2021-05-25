package model;

import com.jfoenix.controls.JFXTextArea;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

/* GameManager.java
   Handles an instance of the Deadwood Game.
   Sets up Deadwood and manages turns and rounds (days)
   Handles end game processing  */

public final class GameManager {

    private static GameManager instance;
    private GameBoard board;

    // View
    private static Scene scene;
    private Parent menuRoot;

    // Game Setttings
    private int playerCount;
    private int currDay = 0;
    private int totalDays;
    private boolean forceSucceed = false;

    // Player Queue and Active Player
    private LinkedList<Player> players = new LinkedList<Player>();
    private Player activePlayer; //Index of active player in list

    // Turn Tracking
    public boolean hasUpgraded = false;
    public boolean hasChosen = false;
    public Boolean hasMoved = false;
    public Boolean hasActed = false;

    // DialogBox
    private JFXTextArea outputField;


    private GameManager() throws IOException {
        board = GameBoard.getInstance();
    }

    public static GameManager getInstance() throws IOException {
        if(instance == null){
            instance = new GameManager();
        }
        return instance;
    }

    public void setupGame(int playerCount, int days, String[] playerIcons) throws IOException {
        this.playerCount = playerCount;
        this.totalDays = days;
        signUpPlayers(playerIcons);
        newDay();
    }

    // Add players to game given player count requested by user and colors chosen
    // Players are named after the color they choose.
    private boolean signUpPlayers (String[] playerIcons) throws IOException {
        String name = null;
        for(int i= 0; i < playerCount; i++){
            String color = playerIcons[i];
            if(color.charAt(0) == 'b'){
                name =  "Blue";
            } else if(color.charAt(0) == 'r'){
                name = "Red";
            } else if(color.charAt(0) == 'v'){
                name = "Violet";
            } else if(color.charAt(0) == 'p'){
                name = "Pink";
            } else if(color.charAt(0) == 'g'){
                name = "Green";
            } else if(color.charAt(0) == 'y'){
                name = "Yellow";
            } else if(color.charAt(0) == 'o'){
                name = "Orange";
            } else if(color.charAt(0) == 'c'){
                name = "Cyan";
            }
            ImageView playerIcon = (ImageView) scene.lookup("#player_"+(i+1));
            playerIcon.setImage(new Image("view/resources/dice/" + playerIcons[i]));
            players.add(new Player(name, playerCount, playerIcon));
        }
        return true;
    }

    //Starts a new turn for the player at the given index
    public void initiateTurn(int index)  {
        outputField = (JFXTextArea) GameManager.getScene().lookup("#output_field");
        activePlayer = players.get(index);

        outputField = (JFXTextArea) GameManager.getScene().lookup("#output_field");
        // Set Text fields to current player stats. For future: Use String bindings.
        Text player_text = (Text) GameManager.getScene().lookup("#active_player");
        player_text.setText(activePlayer.getName());

        Text player_credits = (Text) GameManager.getScene().lookup("#credits");
        player_credits.setText(String.valueOf(activePlayer.getCredits()));

        Text player_dollars = (Text) GameManager.getScene().lookup("#dollars");
        player_dollars.setText(String.valueOf(activePlayer.getDollars()));

        Text player_chips = (Text) GameManager.getScene().lookup("#chips");
        player_chips.setText(String.valueOf(activePlayer.getChips()));

        Text player_location = (Text) GameManager.getScene().lookup("#location");
        player_location.setText(activePlayer.getLocation().getName());

        Text player_role = (Text) GameManager.getScene().lookup("#current_role");
        player_role.setText(activePlayer.getRoleTitle());

        Text current_rank = (Text) GameManager.getScene().lookup("#current_rank");
        current_rank.setText(String.valueOf(activePlayer.getRank()));

        outputField.setText("Your turn " + activePlayer.getName());
    }

    //Checks if the day is over, otherwise changes active player to next player in queue
    public void processEndTurn() throws IOException {
        hasActed = false;
        hasMoved = false;
        hasChosen = false;
        if(board.getNumOpenScenes() > 1){
            int index = players.indexOf(activePlayer);
            if(index == (playerCount-1)){
                index = 0;
            } else {
                index++;
            }
            initiateTurn(index);
        } else if(currDay <= totalDays){
            newDay();
        } else {
            endGame();
        }
    }

    // Start a new day
    public void newDay () throws IOException {
        //Reset lingering stats
        this.hasActed = false;
        this.hasChosen = false;
        this.hasMoved = false;
        this.hasUpgraded = false;

        //Update day and text on screen
        currDay++;
        Text curr_day = (Text) scene.lookup("#current_day");
        curr_day.setText(String.valueOf(currDay));

        //This check handles if user chooses to skip the last day
        if(currDay > totalDays){
            endGame();
            return;
        }

        board.newDay();
        playersToTrailers();
        initiateTurn(0);
    }

    // Return players to the Trailer. Happens at the start of every day
    private void playersToTrailers () throws IOException {
        for(Player player:players){
            if(player.isActing()){
                player.getRole().reset();
            }
            player.respawn();
        }
    }

    // Determines and returns the winner Deadwood
    private void endGame () throws IOException {
        boolean tie = false;
        Player winner = null;
        int highScore = 0;
        LinkedList<Player> winners = new LinkedList<>(); // For the rare chance the game ends in a tie
        for(Player player: players){
            int playerScore = player.calculateScore();
            if(playerScore > highScore){
                winners = new LinkedList<>();
                tie = false;
                winner = player;
                highScore = playerScore;
            } else if(playerScore == highScore && highScore != 0){
                if (!tie) {
                    winners.add(winner);
                }
                tie = true;
                winners.add(player);
            }
        }

        reset();

        //Set the scene back to main menu and display the winner in textview
        scene.setRoot(menuRoot);
        Text menuText = (Text) scene.lookup("#messages_menu");

        if(tie){
            menuText.setText("There appears to have been a tie! Here are your winners: ");
            winners.forEach(player -> menuText.setText(menuText.getText() + player.getName() + " "));
        } else {
            menuText.setText("Congratulations " + winner.getName() + "!");
        }
    }

    //Links the ImageViews for scene (acting stages) to each location in deadwood
    public void setupViews(Scene scene) {
        this.scene = scene;
        for(Location location: board.getLocations()){
            if(location.getClass() == ActingStage.class){
                ImageView imgView = (ImageView) scene.lookup("#"+ location.getName().replace(" ","_"));
                ((ActingStage) location).setView((imgView));
            }
        }
    }

    //Reset Game & stats
    public void reset() throws IOException {
        playersToTrailers();
        this.playerCount = 0;
        this.currDay = 0;
        this.totalDays = 0;
        this.forceSucceed = false;
        this.players = new LinkedList<>();
        this.activePlayer = null;
        this.hasActed = false;
        this.hasChosen = false;
        this.hasMoved = false;
    }

    public static Scene getScene() {
        return scene;
    }


    //Sets the menu root so GameManager can return to main menu upon game completion
    public void setMenuRoot(Parent menuRoot) {
        this.menuRoot = menuRoot;
    }

    public Player getActivePlayer() {
        return this.activePlayer;
    }

    //Used to force wins on the acting stage
    public void toggleCheat() {
        this.forceSucceed = !forceSucceed;
    }
}