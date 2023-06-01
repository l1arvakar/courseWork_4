package com.example.coursework;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingDeque;


public class Server {
    public static Queue<Connect> connects;
    private ServerSocket serverSocket;

    public Server() {
        connects = new LinkedBlockingDeque<>();
    }

    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean isIncorrect = false;
        do {
            try {
                System.out.print("Enter port: ");
                int port = scanner.nextInt();
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                System.err.println("Can't start server on this port");
                isIncorrect = true;
            }
        } while (isIncorrect);
        try {
            System.out.println("Server started at " + Inet4Address.getLocalHost().getHostAddress() + ":" + serverSocket.getLocalPort());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                connects.add(new Connect(socket));
                new Thread(() -> {
                    Connect connection = connects.peek();
                    try {
                        Message message = connection.receive();
                        connects.remove(connection);
                    } catch (Exception ignore) {
                    }
                }).start();
                while (connects.size() > 1) {
                    Connect connection1 = connects.poll();
                    Connect connection2 = connects.poll();
                    boolean connected = true;
                    try {
                        connection1.send(new Message(Message.MessageType.CONNECT, null));
                    } catch (IOException e) {
                        connected = false;
                        connection1.close();
                    }
                    try {
                        connection2.send(new Message(Message.MessageType.CONNECT, null));
                    } catch (IOException e) {
                        connected = false;
                        connection2.close();
                    }
                    if (!connected) {
                        if (connection1.isConnected()) {
                            connects.add(connection1);
                        }
                        if (connection2.isConnected()) {
                            connects.add(connection2);
                        }
                    } else {
                        new ServerThread(connection1, connection2).start();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}