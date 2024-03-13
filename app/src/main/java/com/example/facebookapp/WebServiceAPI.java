package com.example.facebookapp;

import com.example.facebookapp.returntypes.TokenResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface WebServiceAPI {
    @GET("posts")
    Call<List<Post>> getPosts();

    @GET("{id}/posts")
    Call<List<Post>> getUserPosts(@Path("id") int userId);

    @POST("{id}/posts")
    Call<Integer> createPost(@Body Post post, @Path("id") int userId);

    @PUT("{id}/posts/{pid}")
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
}
