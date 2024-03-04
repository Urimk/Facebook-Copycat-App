package com.example.facebookapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Post.class}, version = 1)
public abstract class AppDB extends RoomDatabase {
    private static AppDB INSTANCE;
    public static void initialize(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDB.class, "app_db")
                    .build();
        }
    }

    public static AppDB getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("DATABASE NOT INITIALIZED");
        }
        return INSTANCE;
    }
    public abstract PostDao postDao();

}
