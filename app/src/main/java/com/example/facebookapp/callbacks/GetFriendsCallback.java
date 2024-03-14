package com.example.facebookapp.callbacks;

import java.util.List;

public interface GetFriendsCallback {
    void onSuccess(List<Integer> friendsList);
    void onFailure();
}
