package model;

// Role.java
// Holds information for a specific role within an acting stage.

public class Role {
    private Player player;
    private int difficulty;
    private String title;
    private String line;
    private boolean onCard;

    public Role (int difficulty, String title, String line, boolean onCard) {
        this.difficulty = difficulty;
        this.title = title;
        this.line = line;
        this.onCard = onCard;
    }

    public String toString () {
        return title;
    }

    public int getDifficulty () {
        return difficulty;
    }

    public String getTitle () {
        return title;
    }

    public String getLine () {
        return line;
    }

    public boolean isOnCard () {
        return onCard;
    }

    public boolean isOpen(){
        return player == null;
    }

    public void reset() {
        try {
            player.setRole(null);
            player.setRehearsalChips(0);
            player = null;
        } catch (NullPointerException e){
            // No player on role to reset
        }
    }

    public void setPlayer(Player player){
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}