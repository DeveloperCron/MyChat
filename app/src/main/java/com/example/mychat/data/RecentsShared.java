package com.example.mychat.data;

import com.example.mychat.classes.UserCard;

public class RecentsShared implements IRecent {
    public static RecentsShared _instance;
    public static RecentsShared getInstance() {
        if (_instance == null) {
            _instance = new RecentsShared();
        }

        return _instance;
    }

    @Override
    public void addRecent(UserCard _userCard) {

    }

    @Override
    public void removeRecent(UserCard _userCard) {

    }
}
