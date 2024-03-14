package com.example.facebookapp;

import android.util.Log;

import com.example.facebookapp.callbacks.AddFriendCallback;
import com.example.facebookapp.callbacks.GetFriendsCallback;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendApi {


    Retrofit retrofit;
    WebServiceAPI webServiceAPI;
    private final String BASE_URL = "http://10.0.2.2:3001/api/";

    FriendApi() {
        // add jwt to requests with some kind of middleware
        AuthInterceptor interceptor = new AuthInterceptor();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(interceptor);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }
    void add(int friendId, AddFriendCallback callback) {
        Call<Void> call = webServiceAPI.addFriend(friendId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.v("friendapi fail", "failed to add friend");
                    callback.onFailure();
                }
                else {
                    callback.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("friendapi fail", "failed to add friend", t);
                callback.onFailure();
            }
        });
    }

    void remove(int userId, int friendId, AddFriendCallback callBack) {
        Call<Void> call = webServiceAPI.deleteFriend(userId, friendId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    callBack.onFailure();
                }
                else {
                    callBack.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callBack.onFailure();
            }
        });
    }

    void getFriends(int userId, GetFriendsCallback callback) {
        Call<List<Integer>> call = webServiceAPI.getFriends(userId);
        call.enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (!response.isSuccessful()) {
                    callback.onFailure();
                }
                else {
                    callback.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {

            }
        });

    }
}
