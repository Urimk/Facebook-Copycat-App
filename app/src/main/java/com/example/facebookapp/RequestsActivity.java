package com.example.facebookapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.facebookapp.callbacks.GetFriendsCallback;
import com.example.facebookapp.callbacks.GetUserCallback;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends Activity implements GetUserCallback {

    private ListView requestsListView;
    private int userId;
    private List<FriendRequest> friendRequests;
    private RequestAdapter requestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        requestsListView = findViewById(R.id.requestsListView);
        friendRequests = new ArrayList<>();

        userId = getIntent().getIntExtra("userId", -1);
        Log.v("userid: ", userId + "");
        requestAdapter = new RequestAdapter(this, friendRequests, userId);
        requestsListView.setAdapter(requestAdapter);

        // Set adapter for the requests list
        UserApi userApi = new UserApi();
        userApi.getUser(userId, true, this);


        // Set click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close this activity and return to previous activity
            }
        });

    }

    @Override
    public void onSuccess(User user, boolean isLoggedUser) {
        if (isLoggedUser) {
            List<Integer> requests = user.getFriendRequests();
            for (Integer request: requests
                 ) {
                // get info about all of the requesters
                UserApi userApi = new UserApi();
                userApi.getUser(request, false, this);
            }
        }
        else {
            friendRequests.add(new FriendRequest(user.getUserNick(), user.getUserPfp(), user.getUserId()));
        }
        requestAdapter.changeRequests(friendRequests);
        requestAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure() {

    }
}
