package model;

import java.util.LinkedList;

// Location.java
// Abstract Class defining a location.
// A location can be an acting stage, or the Trailers, or the Casting Office.
// Allows us to group all locations on map and manage neighbors regardless of type.

abstract public class Location {
    private LinkedList<Location> neighbors;
    private LinkedList<String> tempNeighbors = new LinkedList<String>();
    private String name;

    public Location(String name){
        this.name = name;
        neighbors = new LinkedList<>();
    }

    public boolean isNeighbor(Location location){
        if(neighbors.contains(location)){
            return true;
        }
        return false;
    }

    public String getName () {
        return name;
    }

    public String toString () {
        return name;
    }

    // Used to assign neighbors by GameBoard upon game init
    public void addNeighbor(Location loc){
        neighbors.add(loc);
    }

    public void addNeighbor(String name) {
        tempNeighbors.add(name);
    }

    public boolean equals(Location location){
        if(location.name.equals(this.name)){
            return true;
        } else {
            return false;
        }
    }

    public LinkedList<String> getStringNeighbors () {
        return tempNeighbors;
    }

    public void printNeighbors() {
        System.out.println(neighbors.toString());
    }

}