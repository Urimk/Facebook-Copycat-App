package com.example.facebookapp;

public class FriendRequest {
    private String userNick;
    private String userPfp;
    private int userId;
    public FriendRequest(String userNick, String userPfp, int userId) {
        this.userNick = userNick;
        this.userPfp = userPfp;
        this.userId = userId;
    }

    public String getUserNick() {
        return userNick;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserPfp() {
        return userPfp;
    }
}
