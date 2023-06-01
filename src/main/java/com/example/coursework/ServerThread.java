package com.example.coursework;

import java.io.IOException;
import java.util.Random;

public class ServerThread extends Thread {
    private Connect connection1;
    private Connect connection2;
    private boolean isRunning;
    private int ships1;
    private int ships2;

    public ServerThread(Connect connection1, Connect connection2) {
        this.connection1 = connection1;
        this.connection2 = connection2;
        System.out.println("Server thread started");
        isRunning = true;
    }

    @Override
    public void run() {
        try {
            connection1.send(new Message(Message.MessageType.CONNECT, null));
            connection2.send(new Message(Message.MessageType.CONNECT, null));
        } catch (Exception e) {
            isRunning = false;
        }
        ships1 = 10;
        ships2 = 10;
        try {
            new Thread(this::handleConnection1).start();
            new Thread(this::handleConnection2).start();
        } catch (Exception e) {
            isRunning = false;
        }
        while (isRunning) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {

                connection1.send(new Message(Message.MessageType.CONNECT, null));
                connection2.send(new Message(Message.MessageType.CONNECT, null));
            } catch (IOException e) {
                isRunning = false;
            }
        }
        try {
            connection1.send(new Message(Message.MessageType.DISCONNECT, null));
            connection1.close();
            connection2.send(new Message(Message.MessageType.DISCONNECT, null));
            connection2.close();
        } catch (IOException e) {

        }
        System.out.println("Thread end");
    }

    private void handleConnection1() {
        while (isRunning) {
            try {
                Message message1 = connection1.receive();
                System.out.println("User1-send: " + message1.getType().name());
                if (message1.getType() == Message.MessageType.GAME_START){
                    sendGameStart();
                }
                if (message1.getType() == Message.MessageType.SHIP_DESTROYED) {
                    ships1--;
                }
                if (ships1 < 1) {
                    connection2.send(message1);
                    connection2.send(new Message(Message.MessageType.VICTORY, message1.getData()));
                    connection1.send(new Message(Message.MessageType.LOOS, null));
                    System.out.println("Server: WIN/LOOS");
                    isRunning = false;
                } else {
                    connection2.send(message1);
                    System.out.println("User2-receive: " + message1.getType().name());
                }
            } catch (Exception e) {
                isRunning = false;
            }
        }
    }

    private void sendGameStart() throws IOException {
        Random rnd = new Random();
        int random = rnd.nextInt(2);
        connection1.send(new Message(Message.MessageType.GAME_START, random));
        connection2.send(new Message(Message.MessageType.GAME_START, 1 - random));

    }

    private void handleConnection2() {
        while (isRunning) {
            try {
                Message message2 = connection2.receive();
                System.out.println("User2: " + message2.getType().name());
                if (message2.getType() == Message.MessageType.GAME_START){
                    sendGameStart();
                }
                if (message2.getType() == Message.MessageType.SHIP_DESTROYED) {
                    ships2--;
                }
                if (ships2 < 1) {
                    connection1.send(message2);
                    connection1.send(new Message(Message.MessageType.VICTORY, message2.getData()));
                    connection2.send(new Message(Message.MessageType.LOOS, null));
                    System.out.println("Server: WIN/LOOS");
                    isRunning = false;
                } else {
                    connection1.send(message2);
                }
            } catch (Exception e) {
                isRunning = false;
            }
        }
    }
}