package com.example.facebookapp;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PostAPI {
    private MutableLiveData<List<Post>> postListData;
    private PostDao dao;
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;
    private static final int HOME_PAGE_FEED = -1;
    //the id of the user we're currently viewing posts of
    private int feedUserId = HOME_PAGE_FEED;
    //the id of the user currently logged into the app
    private int loggedInUserId;

    private final String BASE_URL = "http://localhost:80";

    public PostAPI(MutableLiveData<List<Post>> postListData, PostDao postDao, int loggedInUserId) {
        this.postListData = postListData;
        this.dao = postDao;

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(webServiceAPI.getClass());
        this.loggedInUserId = loggedInUserId;
    }

    public PostAPI(MutableLiveData<List<Post>> postListData, PostDao postDao, int loggedInUserId,
                   int feedUserId) {
        this(postListData, postDao, loggedInUserId);
        this.feedUserId = feedUserId;
    }

    public void get() {
        Call<List<Post>> call;
        if (this.feedUserId == HOME_PAGE_FEED) {
            call = webServiceAPI.getPosts();
        } else {
            call = webServiceAPI.getUserPosts(this.feedUserId);
        }
        call.enqueue((new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!response.isSuccessful()) {
                    // response failed handle failure, get a new token(?)
                    Log.v("postApi fail", "failed to get posts");
                }
                new Thread(() -> {
                    if (response.body() != null) {
                        if (feedUserId == HOME_PAGE_FEED) {
                            dao.clear();
                            // convert returned collection to an array for insert
                            dao.insert(response.body().toArray(new Post[0]));
                            postListData.postValue(dao.index());
                        } else {
                            // if current feed is a user's wall do not save to local db
                            postListData.postValue(response.body());
                        }
                    }
                }).start();
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {

            }
        }));
    }

    public void add(Post post) {
        Call<Integer> call = webServiceAPI.createPost(post, loggedInUserId);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                //post was added remotely, add to local storage
                if (!response.isSuccessful()) {
                    // response failed handle failure, get a new token(?)
                    Log.v("postApi fail", "failed to add post");
                }
                new Thread(() -> {
                    Integer postId = response.body();
                    post.setPostId(postId.intValue());
                    dao.insert(post);
                    List<Post> posts = postListData.getValue();
                    if (posts != null) {
                        posts.add(post);
                    }
                    postListData.postValue(posts);
                });
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
            }
        });
    }

    public void delete(Post post) {
        Call<Void> call = webServiceAPI.deletePost(loggedInUserId, post.getPostId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    // response failed handle failure, get a new token(?)
                    Log.v("postApi fail", "failed to delete post");
                }
                new Thread(() -> {
                    dao.delete(post);
                    List<Post> posts = postListData.getValue();
                    if (posts != null) {
                        posts.remove(post);
                    }
                    postListData.postValue(posts);
                });

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    public void update(Post post) {
        Call<Void> call = webServiceAPI.updatePost(post, loggedInUserId, post.getPostId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    // response failed handle failure, get a new token(?)
                    Log.v("postApi fail", "failed to update post");
                }
                new Thread(() -> {
                    dao.update(post);
                    List<Post> posts = postListData.getValue();
                    if (posts != null) {
                        int i = 0;
                        for (i = 0; i < posts.size(); i++) {
                            if (posts.get(i).getPostId() == post.getPostId()) {
                                break;
                            }
                        }
                        //replace the old post with the new one
                        posts.set(i, post);
                        postListData.postValue(posts);
                    }
                });

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    public void addComment(Comment comment, Post fatherPost) {
        Call<Integer> call = webServiceAPI.addComment(comment, fatherPost.getAuthorId(), fatherPost.getPostId());
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (!response.isSuccessful()) {
                    // response failed handle failure, get a new token(?)
                    Log.v("postApi fail", "failed to add comment");
                }
                Integer commentId = response.body();
                comment.setCommentId(commentId.intValue());
                fatherPost.addComment(comment);
                new Thread(() -> {

                    dao.update(fatherPost);
                    List<Post> posts = postListData.getValue();
                    if (posts != null) {
                        int i = 0;
                        for (i = 0; i < posts.size(); i++) {
                            if (posts.get(i).getPostId() == fatherPost.getPostId()) {
                                break;
                            }
                        }
                        //replace the old post with the new one
                        posts.set(i, fatherPost);
                        postListData.postValue(posts);
                    }
                });

            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
            }
        });

    }
}
