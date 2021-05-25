// This controller handles user input from the main menu scene
// main_menu.
package controller;

import com.jfoenix.controls.JFXSlider;
import model.GameManager;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

/*
MenuController.java
Controller Class for the Main Menu

Handles onClick listeners for buttons and map navigation in the menu.
The following methods are called upon clicking GUI elements */

public class MenuController {
    private Scene scene; // reference to the window scene
    private Parent gameRoot; // reference to the deadwood game root
    private String[] colorOptions = {"b1.png", "c1.png", "g1.png", "o1.png", "p1.png", "r1.png", "v1.png", "y1.png"};
    private int[] players = {0,1,2,-1,-1,-1,-1,-1};

    private boolean randomize_board = false; //TODO: random board tile layout is not yet implemented but logic is here.
    private int playerCount = 3;

    //Sets up game manager for new game on button click
    public void onLaunchGame() throws Exception {

        if(uniqueColors()){
            //Get Values from menu options
            JFXSlider daySlider = (JFXSlider)scene.lookup("#day_count");
            JFXSlider playerSlider = (JFXSlider)scene.lookup("#player_count");

            int days = (int) daySlider.getValue();
            playerCount = (int) playerSlider.getValue();

            scene.setRoot(gameRoot); //Sets the scene to the deadwood game
            String[] playerIcons = new String[playerCount];

            for(int i = 0; i < playerCount; i++){
                playerIcons[i] = colorOptions[players[i]];
            }

            //Start a new game
            GameManager gm = GameManager.getInstance();
            gm.setupViews(scene);
            gm.setupGame(playerCount, days, playerIcons);
        } else {
            Text menuText = (Text) scene.lookup("#messages_menu");
            menuText.setText("Please choose unique colors for each player");
        }
    }

    //Checks if players have chosen unique colors by index
    private boolean uniqueColors() {
        for(int i = 0; i < playerCount; i++){
            for(int j = i + 1; j < playerCount; j++){
                if(players[i] == players[j]){
                    return false;
                }
            }
        }
        return true;
    }


    //Toggle player color on click
    public void onPlayerClick(MouseEvent e){
        int player = Integer.parseInt(e.getSource().toString().substring(17,18));
        if(player <= playerCount){
            ImageView pIcon = (ImageView) scene.lookup("#icon" + player);
            if(players[player-1] == 7){
                players[player-1] = 0;
            } else {
                players[player-1]++;
            }
            pIcon.setImage(new Image("view/resources/dice/" + colorOptions[players[player-1]]));
        }
    }

    //Adds players to game when player changes the value of player slider.
    //Adds icons to screen or removes them based on count
    public void onPlayerChanged(){
        JFXSlider playerSlider = (JFXSlider)scene.lookup("#player_count");
        this.playerCount = (int) playerSlider.getValue();
        for(int i = 0; i < playerCount;i++){
            players[i] = i;
            ImageView pIcon = (ImageView) scene.lookup("#icon" + (i+1));
            pIcon.setImage(new Image("view/resources/dice/" + colorOptions[i]));
        }

        for(int i = playerCount; i < 8; i++){
            ImageView pIcon = (ImageView) scene.lookup("#icon" + (i+1));
            pIcon.setImage(null);
            players[i] = -1;
        }
    }

    //Handles exiting main menu
    public void onExit(){
        System.exit(0);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setRootGame(Parent gameRoot) {
        this.gameRoot = gameRoot;
    }


}
