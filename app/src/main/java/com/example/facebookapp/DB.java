package com.example.facebookapp;

import android.content.Context;

public class DB {
    static PostsDB postsDB = null;
    static UsersDB usersDB = null;
    public DB(Context context) {
        postsDB = new PostsDB(R.raw.posts, context);
        usersDB = new UsersDB();
    }

    public static PostsDB getPostsDB() {
        return postsDB;
    }

    public static UsersDB getUsersDB() {
        return usersDB;
    }
}
