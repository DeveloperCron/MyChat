package com.example.mychat.constants;

public class Tags {

    public static class Firebase {
        public static final String AUTH = "FIREBASE_AUTH_TAG";
        public static final String FIRESTORE = "FIRESTORE_TAG";
        public static final String USERS_COLLECTION = "_users";
        public static final String CHATS = "chats";
        public static final String CHAT_KEY = "chat_key";
        public static final String MESSAGES = "messages";
        public static final String TIMESTAMP = "timestamp";
    }

    public static class FirebaseErrors {
        public static final String INVALID_EMAIL = "ERROR_INVALID_EMAIL";
        public static final String USER_NOT_FOUND = "ERROR_USER_NOT_FOUND";
        public static final String WRONG_PASSWORD = "ERROR_WRONG_PASSWORD";
        public static final String EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE";

        // Realtime Database Errors
        public static final String PERMISSION_DENIED = "ERROR_PERMISSION_DENIED";
        public static final String NETWORK_ERROR = "ERROR_NETWORK";

        // Firestore Errors
        public static final String DOCUMENT_NOT_FOUND = "ERROR_DOCUMENT_NOT_FOUND";
        public static final String ALREADY_EXISTS = "ERROR_ALREADY_EXISTS";
        public static final String UNAVAILABLE = "ERROR_UNAVAILABLE";
        public static final String DEADLINE_EXCEEDED = "ERROR_DEADLINE_EXCEEDED";
    }

    public static class SharedPreferences {
        public static final String AUTHENTICATION = "AUTHENTICATION_STORAGE";
        public static final String EMAIL = "EMAIL";
        public static final String PASSWORD = "PASSWORD";
    }

    public static class Debugger {
        public static final String KEY = "DEBUG";
    }

    public static class UserFields {
        public static final String FIRST_NAME = "first_name";
        public static final String SECOND_NAME = "second_name";
        public static final String EMAIL = "email";
        public static final String PROFILE_PICTURE = "photo";
        public static final String UID = "uid";
        public static final String FRIENDS = "friends";
    }

    public static class IntentKeys {
        public static final String USER = "USER";
        public static final String CHAT_USER = "CHAT_USER";
        public static final String UID = "UID";
    }

    public static class MessageFields {
        public static final String SENDER = "sender";
        public static final String TEXT = "message";
        public static final String TIMESTAMP = "timestamp";
    }
}
