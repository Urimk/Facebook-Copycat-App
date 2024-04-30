package com.example.facebookapp;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class PostsViewModel extends ViewModel {
    private PostsRepository repository;
    private LiveData<List<Post>> posts;

    public PostsViewModel(int loggedInUserId, int feedUserId, Context context) {
        if (feedUserId == -1) {
            this.repository = new PostsRepository(loggedInUserId, context);
        }
        else {
            this.repository = new PostsRepository(loggedInUserId, feedUserId, context);
        }
        this.posts = this.repository.getAll();
    }

    public LiveData<List<Post>> get() {
        return this.posts;
    }

    public void add(Post post) {
        this.repository.add(post);
    }

    public void delete(Post post) {
        this.repository.delete(post);
    }

    public void edit(Post post) {
        this.repository.update(post);
    }

    public void addComment(Comment comment, Post fatherPost) {
        this.repository.addComment(comment, fatherPost);
    }

    public void reload() {
        this.repository.reload();
    }
}
