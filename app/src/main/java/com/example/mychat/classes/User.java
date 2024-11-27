package com.example.mychat.classes;

public class User {
    public static User _instance;
    private String _name;
    public static User getInstance() {
        if (_instance == null) {
            _instance = new User();
        }

        return _instance;
    }

    public void setName(String name) {
        this._name = name;
    }
}
