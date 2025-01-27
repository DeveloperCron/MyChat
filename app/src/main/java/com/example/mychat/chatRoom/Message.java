package com.example.mychat.chatRoom;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    public enum ModelType {
        SENDER,
        RECEIVER
    }
    private String sender;
    private String text;
    private long timestamp;
    private ModelType messageType;
    public Message(String sender, String text, long timestamp, ModelType messageType) {
        this.sender = sender;
        this.text = text;
        this.timestamp = timestamp;
        this.messageType = messageType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ModelType getMessageType(){
        return this.messageType;
    }

    public String convertTimestampToHour() {
        Date date = new Date(this.timestamp);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        return sdf.format(date);
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
