package model;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

// GameBoard.java
// GameBoard holds the location and relationships between locations and scenes on the map
// Uses XML Parsing to pull titles, names, and lines from files into the game.
// Handles assigning neighbors for navigating the map

public class GameBoard {

    private static GameBoard instance;

    private static LinkedList<SceneCard> sceneCards; 
    private static LinkedList<Location> locations;

    // Non Stage Locations
    private static Location trailer; // Players start here each day
    private static Location casting;

    private GameBoard () throws IOException {
        sceneCards = ParseXML.parseCards();
        locations = ParseXML.parseLocations();
        assignNeighbors();
        trailer = getLocation("Trailer");
        casting = getLocation("Casting Office");
    }

    public static GameBoard getInstance() throws IOException {
        if(instance == null){
            instance = new GameBoard();
        }
        return instance;
    }

    public void newDay () {
        assignSceneCards();
        for (SceneCard sceneCard : sceneCards) {
            sceneCard.reset();
        }
        
        for (Location location : locations) {
            try {
                ((ActingStage)location).reset();
            } catch (Exception e) {
                // not acting stage
            }
        }
    }

    public int getNumOpenScenes() {
        int openScenes = 0;
        for (Location location : locations) {
            try {
                if (((ActingStage)location).isOpen()) {
                    openScenes++;
                }
            } catch (Exception e) {
                // not an acting stage
            }
        }
        return openScenes;
    }

    public void assignNeighbors () {
        // Finishes location initialization, adding neighbor locations to each location based on a temp list of strings of the neighbor names
        for (Location l : locations) {
            LinkedList<String> tempNeighbors = l.getStringNeighbors();
            for (String locName : tempNeighbors) {

                if (locName.equals("office")) {
                    locName = "Casting Office";
                }

                Location neighbor = getLocation(locName); 
                l.addNeighbor(neighbor);
            }
        }
    }

    // Randomize Scene card deck and assign to board
    public void assignSceneCards () {
        Collections.shuffle(sceneCards);
        int curr = 0;
        for (Location location : locations) {
            if (location.getClass() == ActingStage.class) {
                ((ActingStage)location).setSceneCard(sceneCards.get(curr));
            }
            curr++;
        }
    }

    public Location getTrailer () {
        return trailer;
    }

    public Location getLocation (String loc) {
        for (Location l : locations) {
            if (l.getName().equalsIgnoreCase(loc)) {
                return l;
            }
        }
        return null;
    }

    public LinkedList<Location> getLocations() {
        return locations;
    }

}