//FXML Controller Class
//Handles board.fxml, Main DeadWood UI
/*
GameController.java
Main Controller Class for the Deadwood Game

Handles onClick listeners for buttons and map navigation in the game.
The following methods are called upon clicking GUI elements */
package controller;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.*;

import java.io.IOException;
import java.util.LinkedList;

// GameController.java
// Main Controller for Deadwood Game
// Handles GUI events and translates them to actions for GameManager
// Location processing for onClick events allowing player navigation in the map

public class GameController {

    Parent menuRoot; // Reference to main menu root
    private Scene scene; // Reference to the window scene
    @FXML
    ToggleGroup toggle_dollars;
    @FXML
    ToggleGroup toggle_rank;

    //Sets the root for the main menu so we can change scenes on button click or when game ends
    public void setRootMenu(Parent menuRoot){
        this.menuRoot = menuRoot;
    }

    //Checks player turn status and processes act if player is currently acting and hasn't moved yet.
    public void onAct() throws IOException {
        Player activePlayer = GameManager.getInstance().getActivePlayer();
        JFXTextArea output = (JFXTextArea) GameManager.getScene().lookup("#output_field");
        Role role = activePlayer.getRole();
        //Check if player is on a valid role and hasn't already acted or rehearsed.
        if(!activePlayer.isActing()){
            output.setText("You are not currently working on a role...");
            return;
        } else if(GameManager.getInstance().hasActed){
            output.setText("Hold your horses! You have already played your part this turn. Save some lines for the rest of them.");
            return;
        } else if(GameManager.getInstance().hasChosen){
            output.setText("You just chose your role. Your going to have to wait til next turn to try your part.");
            return;
        }

        //Player is okay to act --> check to see if act is successful
        boolean success = activePlayer.act();
        GameManager.getInstance().hasActed = true;
        if(success){
            //Add a shot counter to the map

            String message = "Your act was a success! ";
            if(role.isOnCard()){
                message += "You received two credits.";
            } else { // Off card role
                message += "You received a dollar and a credit.";
            }

            //Check if acting stage is open, or if that was the final shot.
            ActingStage as = (ActingStage) activePlayer.getLocation();
            if(as.isOpen()){
                message += ("\nShots remaining: " + as.getCurrentShot());
            } else { // The last shot was taken
                message += ("\nThat's a wrap! " + as.getName() + " is now closed.");
                message += " Dishing out the bonus money! Check your wallets.";
            }
            output.setText(message);
        } else {
            if(role.isOnCard()){
                output.setText("Your act failed... You get nothing.");
            } else {
                output.setText("Your act failed... But you still get a dollar!");
            }
        }
        updateStats();
    }

    //Checks if player has already acted or rehearsed and is on a valid role, then processes rehearsal
    public void onRehearseClicked() throws IOException {
        JFXTextArea output = (JFXTextArea) GameManager.getScene().lookup("#output_field");
        Player activePlayer = GameManager.getInstance().getActivePlayer();

        if(GameManager.getInstance().hasChosen){
            output.setText("You just chose a role. You have to wait till tomorrow to rehearse.");
            return;
        } else if(GameManager.getInstance().hasActed){
            output.setText("You have already made your move for the day. Leave some lines for the rest of them.");
            return;
        } else if(!activePlayer.isActing()){
            output.setText("You are not currently working on a role!");
            return;
        }

        //Rehearsal is valid, update stats
        activePlayer.rehearse();
        output.setText("You have earned one rehearsal chip!");
        updateStats();
        GameManager.getInstance().hasActed = true;
    }

    //Called after change to stats to update on board
    public void updateStats() throws IOException {
        Text player_credits = (Text) GameManager.getScene().lookup("#credits");
        player_credits.setText(String.valueOf(GameManager.getInstance().getActivePlayer().getCredits()));

        Text player_dollars = (Text) GameManager.getScene().lookup("#dollars");
        player_dollars.setText(String.valueOf(GameManager.getInstance().getActivePlayer().getDollars()));

        Text player_chips = (Text) GameManager.getScene().lookup("#chips");
        player_chips.setText(String.valueOf(GameManager.getInstance().getActivePlayer().getChips()));

        Text player_location = (Text) GameManager.getScene().lookup("#location");
        player_location.setText(GameManager.getInstance().getActivePlayer().getLocation().getName());

        Text player_role = (Text) GameManager.getScene().lookup("#current_role");
        player_role.setText(GameManager.getInstance().getActivePlayer().getRoleTitle());

        Text current_rank = (Text) GameManager.getScene().lookup("#current_rank");
        current_rank.setText(String.valueOf(GameManager.getInstance().getActivePlayer().getRank()));
    }

    //OnClick Method for Processing Upgrade. Checks Toggle Buttons and processes upgrade on player
    public void onUpgradeClicked() throws IOException {
        //Process upgrade :)
        Player player = GameManager.getInstance().getActivePlayer();
        if(!player.getLocation().getName().equals("Casting Office")){
            JFXTextArea output = (JFXTextArea) scene.lookup("#output_field");
            output.setText("You must be in the Casting Office in order to Upgrade");
            return;
        } else {
            JFXRadioButton toggle = (JFXRadioButton) toggle_rank.getSelectedToggle();
            int rank = Integer.parseInt(toggle.getText().substring(5));

            toggle = (JFXRadioButton) toggle_dollars.getSelectedToggle();

            boolean upgradeSuccesful = false;
            if(toggle.getId().equals("toggle_credits")){
                upgradeSuccesful = player.upgrade(rank,false);
            } else {
                upgradeSuccesful = player.upgrade(rank,true);
            }

            JFXTextArea output = (JFXTextArea) scene.lookup("#output_field");
            if(upgradeSuccesful){
                output.setText("You are now rank " + player.getRank());
                GameManager.getInstance().hasUpgraded = true;
            } else {
                output.setText("Your upgrade didn't process. Are you sure you have the dough?");
            }
            updateStats();
        }
    }

    public void onSkipTurn() throws IOException {
        GameManager.getInstance().processEndTurn();
    }

    public void onSkipDay() throws IOException {
        GameManager.getInstance().newDay();
    }

    public void onActClicked() throws IOException {
        if(GameManager.getInstance().getActivePlayer().isActing()){
            onAct();
        } else {

        }
    }

    //Handles a click on the game board. Allows a player to move to a location by clicking it
    //Note this is separate from clicking on a role
    public void onBoardClicked(MouseEvent mouseEvent) throws IOException {

        Player player = GameManager.getInstance().getActivePlayer();
        JFXTextArea textBox = (JFXTextArea) scene.lookup("#output_field");

        //Check if player has the option to move...
        if(GameManager.getInstance().hasActed){
            textBox.setText("You have already acted. Time to end your turn...");
            return;
        }

        if(player.isActing()){
            textBox.setText("You are currently acting. Your only options are to act or rehearse.");
        } else if(GameManager.getInstance().hasMoved == true){
            textBox.setText("You can pick a role within your level or end your turn.");
        } else {
            if(!GameManager.getInstance().getActivePlayer().isActing()) {
                Location location = getLocation(mouseEvent);
                if(!GameManager.getInstance().getActivePlayer().getLocation().equals(location)){
                    if(player.moveTo(location)){
                        ImageView player_icon = player.getIcon();
                        String icon_move = "#icon_" + location.getName().replace(" ","_").toLowerCase();
                        ImageView moveTo = (ImageView) scene.lookup(icon_move);
                        player_icon.setLayoutX(moveTo.getLayoutX());
                        player_icon.setLayoutY(moveTo.getLayoutY());
                        player_icon.toFront();
                        GameManager.getInstance().hasMoved = true;
                        Text locationText = (Text) scene.lookup("#location");
                        locationText.setText(location.getName());
                    } else {
                        textBox.setText("You can only move to neighboring locations!");
                    }
                }
            }
        }
        updateStats();
    }

    //Analyzes the coordinates of a mouse event and translates them to a corresponding location.
    public Location getLocation(MouseEvent mouseEvent) throws IOException {
        Location location;
        double x_val = mouseEvent.getSceneX();
        double y_val = mouseEvent.getSceneY();
        if (x_val > 606 && y_val < 189 || x_val > 886 && y_val < 202 || x_val > 1014 && y_val < 233) {
            location = GameBoard.getInstance().getLocation("Main Street");
        } else if (x_val > 255 && y_val < 242 && x_val < 593) {
            location = GameBoard.getInstance().getLocation("Jail");
        } else if (x_val < 247 && y_val < 250 || x_val < 188 && y_val < 445) {
            location = GameBoard.getInstance().getLocation("Train Station");
        } else if (x_val < 594 && y_val < 442) {
            location = GameBoard.getInstance().getLocation("General Store");
        } else if (x_val < 975 && y_val < 442) {
            location = GameBoard.getInstance().getLocation("Saloon");
        } else if (x_val < 1193 && y_val < 442) {
            location = GameBoard.getInstance().getLocation("Trailer");
        } else if (x_val < 218 && y_val < 669) {
            location = GameBoard.getInstance().getLocation("Casting Office");
        } else if (x_val < 593 && y_val < 691 && x_val > 236) {
            location = GameBoard.getInstance().getLocation("Ranch");
        } else if (x_val < 991 && y_val < 645) {
            location = GameBoard.getInstance().getLocation("Bank");
        } else if (x_val > 1005 && y_val > 460 || x_val > 945 && y_val > 661) {
            location = GameBoard.getInstance().getLocation("Hotel");
        } else if (x_val < 932 && x_val > 607 && y_val > 665) {
            location = GameBoard.getInstance().getLocation("Church");
        } else {
            location = GameBoard.getInstance().getLocation("Secret Hideout");
        }
        return location;
    }

    //Handles selecting an off card role. Checks if player is in the location and if role is open.
    public void onPartClicked(MouseEvent mouseEvent) throws IOException {
        JFXTextArea textBox = (JFXTextArea) scene.lookup("#output_field");
        ActingStage location = (ActingStage) getLocation((MouseEvent) mouseEvent);
        Player player = GameManager.getInstance().getActivePlayer();

        if(player.isActing()){
            textBox.setText("You are already working on a role. You can only act or rehearse.");
            return;
        }

        //Check if player has the option to move...
        if(GameManager.getInstance().hasActed){
            textBox.setText("You have already acted. Time to end your turn...");
            return;
        }

        //Check if player is at the location required for the role chosen
        if(!player.getLocation().equals(location)){
            textBox.setText("That part is too far away! Try moving closer.");
            return;
        }

        if(!location.isOpen()){
            textBox.setText("This location is already closed, try moving to a new location");
            return;
        }

        ImageView playerIcon = player.getIcon();
        Button source = (javafx.scene.control.Button) mouseEvent.getSource();

        Role role = location.getRole(source.getId().replace("_"," "));
        if(!role.isOpen()) {
            textBox.setText("That role has already been taken.");
            return;
        }

        if(role.getDifficulty() > player.getRank()){
            textBox.setText("That role is too difficult for you. Rank up and try again.");
            return;
        }

        //Set player to new role and move player icon
        player.setRole(role);
        double xLoc = source.getLayoutX();
        double yLoc = source.getLayoutY();
        playerIcon.setLayoutX(xLoc);
        playerIcon.setLayoutY(yLoc);
        textBox.setText("You have selected Role: " + role.getTitle() + "\n" + role.getLine());
        GameManager.getInstance().hasChosen = true;
        updateStats();
    }

    //Handles Scene Card Clicks on the Acting Stage
    public void onActingStageClicked(MouseEvent mouseEvent) throws IOException {

        JFXTextArea textBox = (JFXTextArea) scene.lookup("#output_field");

        //Get Location info for scene card
        ActingStage location = (ActingStage) getLocation(mouseEvent);
        LinkedList<Role> roles = location.getOnCardRoles();
        ImageView source = (ImageView) mouseEvent.getSource();

        //Get Player info
        Player player = GameManager.getInstance().getActivePlayer();
        ImageView playerIcon = player.getIcon();


        if(!player.getLocation().equals(getLocation(mouseEvent)) || player.isActing() || GameManager.getInstance().hasChosen
            || GameManager.getInstance().hasActed){
            return; // Player cannot select a scene card act
        }

        if(!location.isOpen()){
            //Location is closed. No response to mouse event
            return;
        }

        //Get the location of the mouse and scene card that was clicked to compute role values
        double sceneCardX = source.getLayoutX();
        double sceneCardY = source.getLayoutY();
        double mouseX = mouseEvent.getSceneX();
        double mouseY = mouseEvent.getSceneY();

        int roleCount = roles.size(); // Role placement depends on role size
        Role role = null;

        //Find which role was selected by mouse location on scene card
        if(roleCount == 1){
            if((mouseX > sceneCardX+82) && (mouseX < sceneCardX + 123) && (mouseY > sceneCardY +49) && (mouseY < sceneCardY + 87)){
                if(roles.get(0).getDifficulty() > player.getRank()){
                    role = roles.getFirst();
                    sceneCardX+=80;
                }
            }
        } else if(roleCount == 2) {
            if ((mouseX > sceneCardX + 53 && mouseX < sceneCardX + 94) && (mouseY > sceneCardY + 49 && mouseY < sceneCardY + 87)) {
                role = roles.getFirst();
                sceneCardX+=51;
            } else if ((mouseX > sceneCardX + 115 && mouseX < sceneCardX + 155) && (mouseY > sceneCardY + 49 && mouseY < sceneCardY + 87)) {
                role = roles.getLast();
                sceneCardX+=113;
            }
        } else if(roleCount == 3){
            if((mouseX > sceneCardX+21 && mouseX < sceneCardX + 61) && (mouseY > sceneCardY + 49 && mouseY < sceneCardY + 87)){
                role = roles.getFirst();
                sceneCardX+=19;
            } else if((mouseX > sceneCardX + 82 && mouseX < sceneCardX  + 123) && (mouseY > sceneCardY+49 && mouseY < sceneCardY+87)){
                role = roles.get(1);
                sceneCardX+=81;
            } else if((mouseX > sceneCardX + 144 && mouseX < sceneCardX+185) && (mouseY > sceneCardY + 49 && mouseY < sceneCardY + 87)){
                role = roles.getLast();
                sceneCardX+=142;
            }
        }

        if(role == null){
            //User clicked on non-part area
            return;
        }

        if(!role.isOpen()){
            textBox.setText("That role is already taken! Better luck next time.");
            return;
        }

        if(role.getDifficulty() > player.getRank()){
            textBox.setText("That role is too difficult for you. Rank up and try again.");
            return;
        }

        sceneCardY+=45; // scene card offset for dice icons

        //Player qualifies for role. Set role and move player icon
        player.setRole(role);
        playerIcon.setLayoutX(sceneCardX);
        playerIcon.setLayoutY(sceneCardY);

        GameManager.getInstance().hasChosen = true;
        updateStats();
    }

    // All this should do is update the textview revealing the cost when a user selects a rank to upgrade to.
    // Toggle buttons for rank are in group "toggle_rank"
    // Toggle buttons for dollars/credits are in group toggle_currency
    public void onToggle() throws IOException {
        GameManager.getInstance().toggleCheat();
    }

    public void onEndTurn() throws IOException {
        GameManager.getInstance().processEndTurn();
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void onReturn() throws IOException {
        GameManager.getInstance().reset();
        this.scene.setRoot(menuRoot);
    }
}
