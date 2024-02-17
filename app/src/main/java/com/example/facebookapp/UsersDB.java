package com.example.facebookapp;

import java.util.ArrayList;
import java.util.List;

public class UsersDB {
    public static final int REGISTRATION_FAILED = -1;
    private List<User> users;
    private int nextNewUserId;

    public UsersDB() {
        // unlike posts users are not loaded from a local file
        this.users = new ArrayList<>();
        nextNewUserId = 0;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(this.users);
    }

    public User getUserById(int userId) {
        for (User user: this.users) {
            if (user.getUserId() == userId) {
                return user;
            }

        }
        return null; //user with that id was not found
    }

    public User connectUser(String userName, String password) {
        for (User user: this.users) {
            if (user.getUserName().equals(userName)
            && user.getUserPass().equals(password)) {
                return user;
            }
        }
        return null; //user was not connected
    }

    public User getUserByUserName(String username) {
        // returns a user with a matching username without the password
        // returns null if a user with matching username is not found
        for (User user: this.users) {
            if (user.getUserName().equals(username)) {
                User requestedUser= new User(user.getUserName(), user.getUserPfp(),
                        user.getUserNick(), user.getUserId());
                return requestedUser;
            }
        }
        return null; // a user with that username was not found
    }

    public int addUser(User newUser) {
        for (User user: this.users) {
            if (newUser.getUserName().equals(user.getUserName())) {
                // a user with that name already exists
                return REGISTRATION_FAILED;
            }
        }
        User newerUser = new User(newUser.getUserName(), newUser.getUserPfp(), newUser.getUserNick(),
                this.nextNewUserId, newUser.getUserPass());
        this.nextNewUserId++;
        this.users.add(newerUser);
        return newerUser.getUserId();
    }

}
