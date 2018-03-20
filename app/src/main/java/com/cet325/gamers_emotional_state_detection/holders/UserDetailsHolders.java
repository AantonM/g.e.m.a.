package com.cet325.gamers_emotional_state_detection.holders;

public class UserDetailsHolders {

    private static UserDetailsHolders single_instance = null;

    private String userId;
    private String gameDifficulty;
    private String notes;
    private String timestamp;

    public void setUserDetails(String userId, String gameDifficulty, String notes, String timestamp){
        this.userId = userId;
        this.gameDifficulty = gameDifficulty;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    public String getUserId(){
        return userId;
    }

    public String getGameDifficulty(){
        return gameDifficulty;
    }

    public String getNotes(){
        return notes;
    }

    public String getTimestamp(){
        return timestamp;
    }

    private UserDetailsHolders(){
        userId = null;
        gameDifficulty = null;
        notes = null;
        timestamp = null;
    }

    public static UserDetailsHolders getInstance()
    {
        if (single_instance == null)
            single_instance = new UserDetailsHolders();

        return single_instance;
    }

    public static void clean()
    {
        single_instance = null;
    }
}
