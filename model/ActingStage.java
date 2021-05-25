package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

// ActingStage.java
// Manages the Acting stage including off card and on card roles
// Acting stage has a state, open or closed depending on how many shots are taken

public class ActingStage extends Location {
    private LinkedList<Role> offCardRoles;
    private SceneCard sceneCard;
    private int totalShots;
    private int currentShot;
    private boolean stageOpen;
    private ImageView cardView;

    public ActingStage (String name, int totalShots) {
        super(name);
        this.totalShots = totalShots;
        currentShot = totalShots;
        offCardRoles = new LinkedList<Role>();
    }

    //Shot counter removed upon successful act
    public void removeShotCounter (Player p) {
        ImageView shotCounter = (ImageView) GameManager.getScene().lookup("#" +
                getName().replace(" ","_").toLowerCase() + "_" + ((totalShots+1)-currentShot));
        shotCounter.setOpacity(1);
        currentShot--;
        if (currentShot == 0) {
            closeStage(p);
        }
    }

    //Closes the stage when no shots remain
    public void closeStage(Player p) {
        stageOpen = false; //Close the stage

        //Role # of die equal to stage budget
        LinkedList<Integer> dieRolls = new LinkedList<Integer>();
        for (int i = 0; i < sceneCard.getBudget(); i++) {
            dieRolls.add(p.rollDie());
        }
        //Sort die rolls in descending order to dish out bonuses from high to low
        Collections.sort(dieRolls, Collections.reverseOrder());

        //Get all the players who were on the card and add them to a stack (LIFO = Highest Role first to get bonus)
        LinkedList<Role> roles = sceneCard.getRoles();
        Stack<Player> bonusStack = new Stack<Player>();
        for(Role role: roles){
            if(!role.isOpen()){
                bonusStack.add(role.getPlayer());
            }
            role.reset(); //reset role
        }


        //Disperse die rolls to players in the bonus stack until no dice remain. Wraps around.
        while(!bonusStack.isEmpty() && !dieRolls.isEmpty()){
            Player player = bonusStack.pop();
            int die = dieRolls.removeFirst();
            player.addDollars(die);
            bonusStack.push(player);
        }

        // Provide bonus for off card players equal to off-card difficulty
        for(Role role: offCardRoles){
            if(role.getPlayer() != null){
                role.getPlayer().addDollars(role.getDifficulty());
            }
            role.reset();
        }
        cardView.setImage(null);
    }

    // Getters and Setters Below

    public boolean isOpen() {
        return stageOpen;
    }

    //Returns a role given the title of the role
    public Role getRole(String roleName){
        for(Role role: offCardRoles){
            if(role.getTitle().equals(roleName)){
                return role;
            }
        }
        return null;
    }

    public int getCurrentShot() {
        return currentShot;
    }

    public LinkedList<Role> getOnCardRoles() {
        return sceneCard.getRoles();
    }

    public int getBudget () {
        return sceneCard.getBudget();
    }

    public void addRole (Role r) {
        offCardRoles.add(r);
    }

    public void setSceneCard (SceneCard sc) {
        sceneCard = sc;
        this.cardView.setImage(new Image(sceneCard.getImgPath()));
    }

    public void setView(javafx.scene.image.ImageView imgView){
        this.cardView = imgView;
    }

    public void reset () {
        //Remove shot markers from board
        for(int i = 1; i <= totalShots; i++){
            ImageView shotMarker = (ImageView) GameManager.getScene().lookup("#" + getName()
                    .replace(" ","_").toLowerCase() + "_" + i);
            shotMarker.setOpacity(0);
        }
        currentShot = totalShots;
        stageOpen = true;
    }

    public int getTotalShots() {
        return this.totalShots;
    }
}