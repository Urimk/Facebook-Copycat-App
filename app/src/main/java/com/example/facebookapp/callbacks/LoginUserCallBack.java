package com.example.facebookapp.callbacks;

public interface LoginUserCallBack {
    void onSuccess(String jwt);
    void onFailure();
}
