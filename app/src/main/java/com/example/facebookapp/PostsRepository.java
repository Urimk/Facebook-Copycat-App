package com.example.facebookapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class PostsRepository {
    private PostDao dao;
    private MutableLiveData<List<Post>> postListData;
    private PostAPI api;

    public PostsRepository(int loggedInUserId) {
        AppDB db = AppDB.getInstance();
        dao = db.postDao();
        postListData = new MutableLiveData<>();
        api = new PostAPI(postListData, dao, loggedInUserId);
    }

    public PostsRepository(int loggedInUserId, int feedUserId) {
        AppDB db = AppDB.getInstance();
        dao = db.postDao();
        postListData = new MutableLiveData<>();
        api = new PostAPI(postListData, dao, loggedInUserId, feedUserId);
    }

    public LiveData<List<Post>> getAll() {
        //exposes auto refreshing posts
        return postListData;
    }

    public void add(final Post post) {
        api.add(post);
    }

    public void delete(final Post post) {
        api.delete(post);
    }

    public void update(final Post post) {
        api.update(post);
    }

    public void addComment(Comment comment, Post fatherPost) {
        api.addComment(comment, fatherPost);
    }

    public void reload() {
        api.get();
    }

}
