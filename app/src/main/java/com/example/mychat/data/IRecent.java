package com.example.mychat.data;

import com.example.mychat.classes.UserCard;

public interface IRecent {
    public void addRecent(UserCard _userCard);
    public void removeRecent(UserCard _userCard);
}
