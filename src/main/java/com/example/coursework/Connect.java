package com.example.coursework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connect {
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public Connect(Socket socket) throws IOException {
        this.socket = socket;
        this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        this.objectInputStream = new ObjectInputStream(socket.getInputStream());
    }
    public void send(Message message) throws IOException {
        synchronized (this.objectOutputStream) {
            this.objectOutputStream.writeObject(message);
        }
    }
    public Message receive() throws IOException, ClassNotFoundException {
        synchronized (this.objectInputStream) {
            return (Message) objectInputStream.readObject();
        }
    }
    public boolean isConnected() {
        return !socket.isClosed();
    }
    public void close() throws IOException {
        socket.close();
    }
    public void flush() {
        try {
            objectOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
