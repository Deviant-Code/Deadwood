package model;

import java.util.LinkedList;

// SceneCard.java
// SceneCards hold the role information for the cards dispersed to each acting stage at the beginning of each day

public class SceneCard {
    private LinkedList<Role> roles;
    private int budget;
    private int sceneNumber;
    private String name;
    private String description;
    private String imgPath;

    public SceneCard (int sceneNum, String name, int budget, String description, String imgPath) {
        this.sceneNumber = sceneNum;
        this.budget = budget;
        this.name = name;
        this.description = description;
        this.imgPath = imgPath;
        roles = new LinkedList<Role>();
    }

    public void reset () {
        for (Role role : roles) {
            role.reset();
        }
    }

    public void addRole (Role r) {
        roles.add(r);
    }

    public int getBudget () {
        return budget;
    }

    public LinkedList<Role> getRoles() {
        return roles;
    }

    public String toString () {
        String s = "Name: " + name + ", scene number: " + sceneNumber + ", budget: " + budget;
        s = s + "\nDescription: " + description;
        return s;
    }

    public String getImgPath() {
        return imgPath;
    }
}