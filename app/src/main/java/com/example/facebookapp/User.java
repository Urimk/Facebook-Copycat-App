package com.example.facebookapp;

public class User {
    private String userName;
    private String userPfp;
    private String userNick;
    private int userId;
    private String userPass;

    public User(String userName, String userPfp, String userNick, int userId, String userPass) {
        this.userName = new String(userName);
        this.userPfp = new String(userPfp);
        this.userNick = new String(userNick);
        this.userId = userId;
        this.userPass = new String(userPass);
    }

    public User(String userName, String userPfp, String userNick, String userPass) {
        // use this constructor when making a user (db will give an id)
        this.userName = new String(userName);
        this.userPfp = new String(userPfp);
        this.userNick = new String(userNick);
        this.userPass = new String(userPass);
    }
    public User(String userName, String userPfp, String userNick, int userId) {
        this.userName = new String(userName);
        this.userPfp = new String(userPfp);
        this.userNick = new String(userNick);
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPfp() {
        return userPfp;
    }

    public String getUserNick() {
        return userNick;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserPass() {
        return userPass;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", userPfp='" + userPfp + '\'' +
                ", userNick='" + userNick + '\'' +
                ", userId=" + userId +
                ", userPass='" + userPass + '\'' +
                '}';
    }
}
