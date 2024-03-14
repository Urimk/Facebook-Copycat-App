package com.example.facebookapp;

import com.example.facebookapp.returntypes.TokenResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface WebServiceAPI {
    @GET("posts")
    Call<List<Post>> getPosts();

    @GET("{id}/posts")
    Call<List<Post>> getUserPosts(@Path("id") int userId);

    @POST("{id}/posts")
    Call<Post> createPost(@Body Post post, @Path("id") int userId);

    @PATCH("{id}/posts/{pid}")
    Call<Void> updatePost(@Body Post post, @Path("id") int userId, @Path("pid") int postId);

    @POST("{id}/posts/{pid}/comments")
    Call<Integer> addComment(@Body Comment comment, @Path("id") int userId, @Path("pid") int postId);

    @DELETE("{id}/posts/{pid}")
    Call<Void> deletePost(@Path("id") int userId, @Path("pid") int postId);

    @POST("users")
    Call<User> createUser(@Body User user);

    @POST("tokens")
    Call<TokenResponse> getToken(@Body User user);

    @GET("users/{id}")
    Call<User> getUser(@Path("id") int userId);

    @POST("users/{id}/friends")
    Call<Void> addFriend(@Path("id") int userId);

    @GET("users/{id}/friends")
    Call<List<Integer>> getFriends(@Path("id") int userId);

    @PATCH("users/{id}/friends/{fid}")
    Call<Void> acceptFriend(@Path("id") int userId, @Path("fid") int friendId);

    @DELETE("users/{id}/friends/{fid}")
    Call<Void> deleteFriend(@Path("id") int userId, @Path("fid") int friendId);
}
