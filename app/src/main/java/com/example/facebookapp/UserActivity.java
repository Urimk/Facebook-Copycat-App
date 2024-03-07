package com.example.facebookapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Retrieve user information from the intent
        String userId = getIntent().getStringExtra("userId");
        String username = ""; // Replace with logic to get username based on userId
        String userProfilePicUri = ""; // Replace with logic to get userProfilePicUri based on userId

        // Initialize UI components
        ImageView userProfileImageView = findViewById(R.id.userProfileImageView);
        TextView usernameTextView = findViewById(R.id.usernameTextView);
        Button addFriendButton = findViewById(R.id.addFriendButton);
        Button goBackButton = findViewById(R.id.goBackButton);

        // Set user information to UI components
        // For now, setting a default image and username
        userProfileImageView.setImageResource(R.drawable.default_profile_pic);
        usernameTextView.setText(username);

        // Set click listener for the "Add Friend" button
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement logic to add a friend
                // For example, show a toast message
                // Toast.makeText(UserActivity.this, "Friend request sent", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for the "Go Back" button
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the activity and go back
                finish();
            }
        });
    }
}

