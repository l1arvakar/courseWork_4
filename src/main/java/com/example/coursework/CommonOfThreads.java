package com.example.coursework;

public class CommonOfThreads {
    public static int isHit(int x, int y) {
        for (int i = 0; i < ListOfShip.getInstance().getShips().size(); i++) {
            for (int j = 0; j < ListOfShip.getInstance().getShips().get(i).getCoordinates().size(); j++) {
                if ((ListOfShip.getInstance().getShips().get(i).getCoordinates().get(j).getX() == x) &&
                        (ListOfShip.getInstance().getShips().get(i).getCoordinates().get(j).getY() == y)) {
                    int health = ListOfShip.getInstance().getShips().get(i).getHealth();
                    ListOfShip.getInstance().getShips().get(i).setHealth(health - 1);

                    if (ListOfShip.getInstance().getShips().get(i).getHealth() == 0)
                        ListOfShip.getInstance().setShip_count();

                    return 1;
                }
            }
        }

        return 0;
    }

    public static int isDied(int x, int y) {
        for (int i = 0; i < ListOfShip.getInstance().getShips().size(); i++) {
            for (int j = 0; j < ListOfShip.getInstance().getShips().get(i).getCoordinates().size(); j++) {
                if ((ListOfShip.getInstance().getShips().get(i).getCoordinates().get(j).getX() == x) &&
                        (ListOfShip.getInstance().getShips().get(i).getCoordinates().get(j).getY() == y)) {
                    int health = ListOfShip.getInstance().getShips().get(i).getHealth();

                    if (health == 0)
                        return 1;
                    else
                        return 0;
                }
            }
        }

        return 0;
    }

    public static int getIndex(int x, int y) {
        for (int i = 0; i < ListOfShip.getInstance().getShips().size(); i++) {
            for (int j = 0; j < ListOfShip.getInstance().getShips().get(i).getCoordinates().size(); j++) {
                if ((ListOfShip.getInstance().getShips().get(i).getCoordinates().get(j).getX() == x) &&
                        (ListOfShip.getInstance().getShips().get(i).getCoordinates().get(j).getY() == y)) {
                    return i;
                }
            }
        }

        return 0;
    }
}
