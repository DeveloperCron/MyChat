package com.example.mychat.classes;

public class Chat {
    private String lastMessage;
    private String sender;

    public Chat(String lastMessage, String sender) {
        this.lastMessage = lastMessage;
        this.sender = sender;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "lastMessage='" + lastMessage + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}
