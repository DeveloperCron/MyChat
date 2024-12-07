package com.example.mychat.classes;

public class UserCard {
    private String name;
    private String email;

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public UserCard(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
