package com.example.facebookapp;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PostViewModelFactory implements ViewModelProvider.Factory {
    private int loggedInUserId, feedUserId;
    public PostViewModelFactory(int loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
        this.feedUserId = -1;
    }

    public PostViewModelFactory(int loggedInUserId, int feedUserId) {
        this.loggedInUserId = loggedInUserId;
        this.feedUserId = feedUserId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PostsViewModel.class)) {
            try {
                T viewModel = modelClass.getDeclaredConstructor(int.class, int.class).newInstance(this.loggedInUserId, this.feedUserId);
                return viewModel;
            }
            catch (Exception e) {
                throw new RuntimeException("Error creating PostsViewModel", e);
            }
        }
        throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}
