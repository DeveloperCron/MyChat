package com.example.mychat.utils;

import com.example.mychat.classes.UserSingleton;

public class ChatUtils {
    private static final UserSingleton _userSingleton = UserSingleton.getInstance();

    public static String getSenderUidFromChatKey(String chatKey) {
        String[] parts = chatKey.split("_");
        String firstPart = parts[0];
        String secondPart = parts[1];

        if (_userSingleton.getUid().equals(firstPart)) {
            return secondPart;
        }

        return firstPart;
    }

}
