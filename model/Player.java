package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

// Player Object
// Holds state for player information, location, role and score

public class Player {

    //Player game data
    private String name;
    private int rank;
    private int credits;
    private int dollars;
    private int rehearsalChips;
    private Location location;
    private Role role;

    //Starting coordinates for player icon
    private double trailerX;
    private double trailerY;
    private ImageView icon;

    public Player(String name, int numOfPlayers, javafx.scene.image.ImageView icon) {
        this.name = name;
        //Initiate icon and set value of start position for respawn method
        this.icon = icon;
        this.trailerX = icon.getLayoutX();
        this.trailerY = icon.getLayoutY();
        location = null;
        role = null;
        rehearsalChips = 0;
        rank = 1;
        credits = 0;
        dollars = 0;

        switch (numOfPlayers) {
        case 5:
            credits = 2;
            break;
        case 6:
            credits = 4;
            break;
        case 7:
        case 8:
            rank = 2;
            break;
        }
    }

    public boolean moveTo(Location newLoc) {
        if (this.location.isNeighbor(newLoc)) {
            this.location = newLoc;
            return true;
        }
        return false;
    }

    public ImageView getIcon(){
        return this.icon;
    }

    public boolean upgrade(int buyRank, boolean dollars){

        if(buyRank <= rank){
            return false;
        }

        if(dollars) { //Opt to pay with dollars
            if (this.dollars >= CastingOffice.getCost(buyRank, true)){
                //Player has enough money! Update their dice to new rank and charge them.
                String iconName = this.icon.getImage().getUrl();
                String newIcon = iconName.substring(0,iconName.length()-5) + buyRank + iconName.substring(iconName.length() - 4);
                icon.setImage(new Image(newIcon));
                this.rank = buyRank;
                this.dollars -= CastingOffice.getCost(buyRank, true);
                return true;
            }
        } else {
            //Paying with credits
            if (this.credits >= CastingOffice.getCost(buyRank, false)) {
                //player has enough credits--update and
                String iconName = this.icon.getImage().getUrl();
                String newIcon = iconName.substring(0,iconName.length()-5) + buyRank + iconName.substring(iconName.length() - 4);
                icon.setImage(new Image(newIcon));
                this.rank = buyRank;
                this.credits -= CastingOffice.getCost(buyRank, false);
                return true;
            }
        }
        return false;
    }

    public void rehearse() {
        rehearsalChips++;
    }

    public boolean act() {
        int dieResult = rollDie();

        if (dieResult >= getRollRequiredForSuccess()) {
            succeedAct();
            return true;
        } else {
            failAct();
            return false;
        }
    }

    public int getRollRequiredForSuccess() {
        return ((ActingStage)location).getBudget() - rehearsalChips;
    }

    public int rollDie() {
        double min = 1;
        double max = 6;
        return (int)((Math.random() * ((max - min) + 1)) + min);
    }

    private void succeedAct() {
        if (role.isOnCard()) {
            credits += 2;
        } else {
            dollars++;
            credits++;
        }
        ((ActingStage)location).removeShotCounter(this);
    }

    private void failAct() {
        if (!role.isOnCard()) {
        dollars++;
        } 
    }
    
    public String toString () {
        return this.name;
    }

    // Scoring rules for determining winner
    public int calculateScore(){
        return credits + dollars + (rank * 5);
    }

    // If name hasn't been set, sets it to the color chosen by player
    public String getName() {
        return name;
    }

    public int getRank() {
        return rank;
    }

    public void addDollars (int dollars) {
        this.dollars += dollars;
    }


    public void setRehearsalChips(int rehearsalChips) {
        this.rehearsalChips = rehearsalChips;
    }

    public Location getLocation() {
        return location;
    }

    public void setRole(Role role) {
        this.role = role;
        if(role != null){
            role.setPlayer(this);
        }
    }

    //Sends player and icon back to Trailer
    public void respawn() throws IOException {
        if(isActing()){
            this.role.reset();
            this.role = null;
        }
        this.location = GameBoard.getInstance().getTrailer();
        this.icon.setLayoutY(trailerY);
        this.icon.setLayoutX(trailerX);
    }

    public boolean isActing() {
        if(role == null){
            return false;
        } else {
            return true;
        }
    }

    public int getCredits() {
        return this.credits;
    }

    public int getDollars() {
        return this.dollars;
    }

    public int getChips() {
        return this.rehearsalChips;
    }

    public String getRoleTitle() {
        if(this.role == null){
            return "None";
        }
        return this.role.getTitle();
    }

    public Role getRole(){
        return this.role;
    }
}