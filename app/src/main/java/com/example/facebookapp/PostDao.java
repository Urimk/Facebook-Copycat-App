package com.example.facebookapp;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

public interface PostDao {
    @Query("SELECT * FROM post")
    List<Post> index();

    @Query("SELECT * FROM post WHERE id = :id")
    Post get(int id);

    @Insert
    void insert(Post... posts);

    @Update
    void update(Post... posts);

    @Delete
    void delete(Post... posts);

    @Query("DELETE FROM post")
    void clear();
}
