package com.example.mychat.classes;

import androidx.annotation.Keep;

import com.google.firebase.firestore.FieldValue;

import java.util.List;

@Keep
public class StoredUser {
    private String photo;
    private String first_name;
    private String second_name;
    private String email;
    private List<String> friends;

    public StoredUser() { }
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

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public String getName(){
        return this.first_name + " " + this.second_name;
    }
}
