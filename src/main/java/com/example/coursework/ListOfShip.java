package com.example.coursework;

import java.util.ArrayList;
import java.util.List;

public class ListOfShip {
    private static ListOfShip instance;
    private int ship_count;
    private List<Ship> ships = new ArrayList<>();
    private String whois;

    public static synchronized ListOfShip getInstance() {
        if (instance == null)
            instance = new ListOfShip();
        return instance;
    }

    private ListOfShip(){
        ship_count = 10;
    }

    public List<Ship> getShips() {
        return this.ships;
    }

    public void addShip(Ship ship) {
        this.ships.add(ship);
    }

    public void removeShips() {
        this.ships.clear();
    }

    public int getShip_count() {
        return ship_count;
    }

    public void setShip_count() {
        ship_count--;
    }

    public String getWhois() {
        return whois;
    }

    public void setWhois(String whois) {
        this.whois = whois;
    }
}