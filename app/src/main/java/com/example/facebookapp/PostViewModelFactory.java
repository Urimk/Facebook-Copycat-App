package com.example.facebookapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PostViewModelFactory implements ViewModelProvider.Factory {
    private int loggedInUserId, feedUserId;
    private Context context;
    public PostViewModelFactory(int loggedInUserId, Context context) {
        this.loggedInUserId = loggedInUserId;
        this.feedUserId = -1;
        this.context = context;
    }

    public PostViewModelFactory(int loggedInUserId, int feedUserId, Context context) {
        this.loggedInUserId = loggedInUserId;
        this.feedUserId = feedUserId;
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PostsViewModel.class)) {
            try {
                T viewModel = modelClass.getDeclaredConstructor(int.class, int.class, Context.class).newInstance(this.loggedInUserId, this.feedUserId, this.context);
                return viewModel;
            }
            catch (Exception e) {
                throw new RuntimeException("Error creating PostsViewModel", e);
            }
        }
        throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}
