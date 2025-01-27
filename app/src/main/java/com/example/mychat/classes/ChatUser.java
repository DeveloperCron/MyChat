package com.example.mychat.classes;

import java.io.Serializable;

public class ChatUser implements Serializable {
    private String photo;
    private String first_name;
    private String second_name;
    private String email;
    private String uid;
    private String last_message;

    public ChatUser(String photo, String first_name, String second_name, String email, String uid) {
        this.photo = photo;
        this.first_name = first_name;
        this.second_name = second_name;
        this.email = email;
        this.uid = uid;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getSecond_name() {
        return second_name;
    }

    public void setSecond_name(String second_name) {
        this.second_name = second_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return this.first_name + " " + this.second_name;
    }

    public String getLastMessage() {
        return this.last_message;
    }

    public void setLastMessage(String last_message) {
        this.last_message = last_message;
    }
}
