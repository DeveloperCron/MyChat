package com.example.mychat.classes;

/**
 * Represents a user in the chat application. This class is implemented as a singleton.
 */
public class User {

    private static User instance; // Renamed _instance to instance and made private
    private String name;        // Renamed _name to name
    private String photoUrl;    // Renamed _photoUrl to photoUrl

    /**
     * Gets the singleton instance of the User class.
     *
     * @return The singleton instance.
     */
    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    /**
     * Sets the user's name.
     *
     * @param name The user's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's name.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's photo URL.
     *
     * @param photoUrl The URL of the user's photo.
     */
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * Gets the user's photo URL.
     *
     * @return The URL of the user's photo.
     */
    public String getPhotoUrl() {
        return photoUrl;
    }
}