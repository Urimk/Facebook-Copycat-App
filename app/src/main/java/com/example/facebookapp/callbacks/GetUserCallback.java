package com.example.facebookapp.callbacks;

import com.example.facebookapp.User;

public interface GetUserCallback {
    void onSuccess(User user);
    void onFailure();
}
