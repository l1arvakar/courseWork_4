package com.example.coursework;

import java.io.Serializable;

public class Message implements Serializable {

    public static enum MessageType {
        NONE, CONNECT, DISCONNECT, PLANNING_START, PLANNING_FINISH, GAME_START, HIT, SHIP_WOUNDED, MISS, SHIP_DESTROYED, VICTORY, LOOS
    }

    private Object data;
    private MessageType type;
    public Message(MessageType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Object getData(){
        return data;
    }

    public MessageType getType() {
        return type;
    }
}
