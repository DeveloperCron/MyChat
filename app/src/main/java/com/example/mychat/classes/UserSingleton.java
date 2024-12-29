package com.example.mychat.classes;

import java.util.Collections;
import java.util.List;

/**
 * Represents a user in the chat application. This class is implemented as a singleton.
 */
public class UserSingleton {

    private static UserSingleton instance;

    // User properties
    private String name;
    private String photoUrl;
    private String first_name;
    private String second_name;
    private String email;
    private String uid;
    private List<StoredUser> friends;

    private UserSingleton() {
        this.name = "";
        this.first_name = "";
        this.second_name = "";
        this.email = "";
        this.photoUrl = "";
        this.friends = Collections.emptyList();
    }

    // Singleton instance access
    public static synchronized UserSingleton getInstance() {
        if (instance == null) {
            instance = new UserSingleton();
        }
        return instance;
    }

    // Getters

    public String getName() {
        return name;
    }

    // Setters (to modify user information)
    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getSecondName() {
        return second_name;
    }

    public void setSecondName(String second_name) {
        this.second_name = second_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<StoredUser> getFriends() {
        return friends == null ? Collections.emptyList() : friends;
    }

    public void setFriends(List<StoredUser> friends) {
        this.friends = friends;
    }

    public void resetUser() {
        this.name = "";
        this.first_name = "";
        this.second_name = "";
        this.email = "";
        this.photoUrl = "";
        this.friends = Collections.emptyList();
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
