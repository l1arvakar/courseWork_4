package com.example.coursework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ship implements Serializable {
    private int health;
    private int decks;
    private String orientation;
    private List<Point> coordinates = new ArrayList<>();
    private List<Point> disablePoints = new ArrayList<>();

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getDecks() {
        return decks;
    }

    public void setDecks(int decks) {
        this.decks = decks;
    }

    public List<Point> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Point> coordinates) {
        this.coordinates = coordinates;
    }

    public List<Point> getDisablePoints() {
        return disablePoints;
    }

    public void setDisablePoints(List<Point> disablePoints) {
        this.disablePoints = disablePoints;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
}