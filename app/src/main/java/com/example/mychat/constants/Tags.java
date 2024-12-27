package com.example.mychat.constants;

public class Tags {
    public static class FirebaseTags {
        public static String FIREBASE_AUTH_TAG = "FIREBASE_AUTH_TAG";
        public static String FIREBASE_FIRESTORE_TAG = "FIRESTORE_TAG";
        public static String USERS_COLLECTION = "_users";
    }

    public static class SharedPreferencesTags {
        public static String AUTHENTICATION_TAG = "AUTHENTICATION_STORAGE";
        public static String EMAIL_TAG = "EMAIL";
        public static String PASSWORD_TAG = "PASSWORD";
    }

    public static class DEBUGGER {
        public static String DEBUG_KEY = "DEBUG";
    }

    public static class FIREBASE_DATABASE {
            public static String CHATS_KEY = "chats";
    }

    public static class USER_FIELDS {
        public static String FIRST_NAME = "first_name";
        public static String SECOND_NAME = "second_name";
        public static String EMAIL = "email";
        public static String PROFILE_PICTURE = "photo";
    }
}
