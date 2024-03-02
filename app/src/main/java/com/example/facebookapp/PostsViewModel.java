package com.example.facebookapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class PostsViewModel extends ViewModel {
    private PostsRepository repository;
    private LiveData<List<Post>> posts;

    public PostsViewModel(int loggedInUserId, int feedUserId) {
        repository = new PostsRepository(loggedInUserId, feedUserId);
        posts = repository.getAll();
    }
    public PostsViewModel(int loggedInUserId) {
        repository = new PostsRepository(loggedInUserId);
        posts = repository.getAll();
    }

    public LiveData<List<Post>> get() {
        return posts;
    }

    public void add(Post post) {
        repository.add(post);
    }

    public void delete(Post post) {
        repository.delete(post);
    }

    public void edit(Post post) {
        repository.update(post);
    }

    public void addComment(Comment comment, Post fatherPost) {
        repository.addComment(comment, fatherPost);
    }

    public void reload() {
        repository.reload();
    }
}
