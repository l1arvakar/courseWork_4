package com.example.coursework.client;

import com.example.coursework.CommonOfThreads;
import com.example.coursework.FightController;
import com.example.coursework.ListOfShip;
import com.example.coursework.Point;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientThread extends Thread {
    private static boolean start_communicate = false;
    private static boolean is_connected;
    private static int x = -1, y = -1;
    private static boolean end = false;
    private static FightController fight_controller;
    private static String ip_adress;
    private static Socket socket;
    private static InputStream is;
    private static OutputStream os;

    public ClientThread(String ip) {
        ClientThread.ip_adress = ip;
    }

    public void run() {
        try {
            socket = new Socket(ip_adress, 8880);
            is_connected = true;
            is = socket.getInputStream();
            os = socket.getOutputStream();

            while (!start_communicate) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("ClientThread - " + e);
                    e.printStackTrace();
                }
            }

            int flag = is.read();
            if (flag == 1) {
                os.write(flag);
                System.out.println("Connected to server");

                flag = is.read();
                if (flag == 0) {
                    fight_controller.setDisableEnemyField();
                    waitAnswer();
                }
                else {
                    fight_controller.setEnableEnemyField();
                    sendAnswer();
                }
            }
        } catch (IOException e) {
            is_connected = false;
            System.out.println("ClientThread - " + e);
            e.printStackTrace();
        }
    }

    public static boolean getIsConnected() {
        return is_connected;
    }

    public static void setFightController(FightController controller) {
        ClientThread.fight_controller = controller;
    }

    public static void setStartCommunicate() {
        start_communicate = true;
    }

    private static void closeClient() {
        try {
            is.close();
            os.close();
            socket.close();
            System.out.println("Client closed");
        } catch (IOException e) {
            System.out.println("ClientThread - " + e);
            e.printStackTrace();
        }
    }

    private static void waitAnswer() {
        try {
            int row = is.read();
            int column = is.read();
            int is_hit = CommonOfThreads.isHit(row, column);
            int is_died = CommonOfThreads.isDied(row, column);

            os.write(is_hit);
            os.write(is_died);

            if (is_hit == 1)
                fight_controller.hitClient(row, column);
            else
                fight_controller.no_hitClient(row, column);

            if (is_died == 1)
            {
                int index = CommonOfThreads.getIndex(row, column);
                List<Point> disablePoints = ListOfShip.getInstance().getShips().get(index).getDisablePoints();
                os.write(disablePoints.size());

                for (Point disablePoint : disablePoints) {
                    os.write(disablePoint.getX());
                    os.write(disablePoint.getY());
                }

                if (ListOfShip.getInstance().getShip_count() == 0) {
                    os.write(1);
                    fight_controller.endGame("Lose");
                    end = true;

                    closeClient();
                    return;
                }
                else
                    os.write(0);
            }

            if (!end) {
                if (is_hit == 1) {
                    fight_controller.setDisableEnemyField();
                    waitAnswer();
                } else {
                    fight_controller.setEnableEnemyField();
                    ClientThread.x = ClientThread.y = -1;

                    sendAnswer();
                }
            }

            end = false;
        } catch (IOException e) {
            System.out.println("ClientThread - " + e);
            e.printStackTrace();
        }
    }

    private static void sendAnswer() {
        try {
            while((x == -1) && (y == -1))
                sleep(100);

            os.write(ClientThread.x);
            os.write(ClientThread.y);

            int hit = is.read();
            int kill = is.read();

            if (hit == 1)
                fight_controller.hitEnemy(ClientThread.x, ClientThread.y);
            else
                fight_controller.no_hitEnemy(ClientThread.x, ClientThread.y);

            if (kill == 1)
            {
                int count = is.read();
                List<Point> disablePoints = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    Point point = new Point(is.read(), is.read());
                    disablePoints.add(point);
                }

                fight_controller.paintEnemyDisablePoints(disablePoints);

                if (is.read() == 1) {
                    fight_controller.endGame("Win");
                    end = true;

                    closeClient();
                    return;
                }
            }

            if (!end) {
                if (hit == 1) {
                    ClientThread.x = ClientThread.y = -1;
                    sendAnswer();
                } else {
                    fight_controller.setDisableEnemyField();
                    waitAnswer();
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("ClientThread - " + e);
            e.printStackTrace();
        }
    }

    public static void setPoints(int x, int y) {
        ClientThread.x = x;
        ClientThread.y = y;
    }
}
/*if (health == 0)
  ListOfShip.getInstance().setShip_count();
  else
  ListOfShip.getInstance().getShips().get(i).setHealth(health - 1);*/