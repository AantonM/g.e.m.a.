package com.cet325.gamers_emotional_state_detection.holders;

/**
 * Singleton holder object that stores the user's details
 */
public class UserDetailsHolders {

    private static UserDetailsHolders single_instance = null;

    private String userId;
    private String gameDifficulty;
    private String notes;
    private String timestamp;

    /**
     * Method that stores a new user details.
     *
     * @param userId         String: the user id
     * @param gameDifficulty String: the selected level of difficulty
     * @param notes          String: notes taken
     * @param timestamp      String: a timestamp of the game
     */
    public void setUserDetails(String userId, String gameDifficulty, String notes, String timestamp) {
        this.userId = userId;
        this.gameDifficulty = gameDifficulty;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    /**
     * Method that returns the user ID
     *
     * @return String: user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Method that returns the selected level of difficulty
     *
     * @return String: selected level of difficulty
     */
    public String getGameDifficulty() {
        return gameDifficulty;
    }

    /**
     * Method that returns the notes inserted
     *
     * @return String: notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Method that returns the timestamp
     *
     * @return String: timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Constructor that initialises the fields where the data will be saved.
     */
    private UserDetailsHolders() {
        userId = null;
        gameDifficulty = null;
        notes = null;
        timestamp = null;
    }

    /**
     * Method that initialises the singleton object.
     *
     * @return UserDetailsHolders: the current instance of the object
     */
    public static UserDetailsHolders getInstance() {
        if (single_instance == null)
            single_instance = new UserDetailsHolders();

        return single_instance;
    }

    /**
     * Clear the holder object
     */
    public static void clean() {
        single_instance = null;
    }
}
