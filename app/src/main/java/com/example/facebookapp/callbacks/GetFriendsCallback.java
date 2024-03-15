package com.example.facebookapp.callbacks;

import com.example.facebookapp.FriendRequest;

import java.util.List;

public interface GetFriendsCallback {
    void onSuccess(List<FriendRequest> friendsList);
    void onFailure();
}
