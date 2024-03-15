package com.example.facebookapp;

import android.util.Log;

import com.example.facebookapp.callbacks.AddUserCallback;
import com.example.facebookapp.callbacks.GetUserCallback;
import com.example.facebookapp.callbacks.LoginUserCallBack;
import com.example.facebookapp.returntypes.TokenResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserApi {

    Retrofit retrofit;
    WebServiceAPI webServiceAPI;
    private final String BASE_URL = "http://10.0.2.2:3001/api/";

    UserApi() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }
    void add(User user, AddUserCallback callback) {
        Call<User> call = webServiceAPI.createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Log.v("userApi fail", "failed to add user");
                    callback.onFailure();
                }
                else {
                    callback.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("userApi fail", "failed to add user", t);
                callback.onFailure();
            }
        });
    }

    void loginUser(User user, LoginUserCallBack callBack) {
        Call<TokenResponse> call = webServiceAPI.getToken(user);
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (!response.isSuccessful()) {
                    callBack.onFailure();
                }
                else {
                    assert response.body() != null;
                    callBack.onSuccess(response.body().getString());
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                callBack.onFailure();
            }
        });
    }

    void getUser(int userId, boolean isWallUser, GetUserCallback callback) {
        Call<User> call = webServiceAPI.getUser(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    callback.onFailure();
                }
                else {
                    callback.onSuccess(response.body(), isWallUser);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onFailure();
            }
        });
    }
}
