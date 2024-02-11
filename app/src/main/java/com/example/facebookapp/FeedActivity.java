package com.example.facebookapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private EditText postEditText;
    private ImageButton imageUploadButton;
    private Button postButton;
    private ListView postsListView;

    private User currentUser;  // Sample session user
    private DB database; // Reference to the database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // Initialize the sample session user
        currentUser = new User("SampleUser", "SampleUserPFP", "UserNick", 123, "samplePassword");

        // Initialize the database
        database = new DB(this);

        // Initialize UI components
        postEditText = findViewById(R.id.postEditText);
        imageUploadButton = findViewById(R.id.imageUploadButton);
        postButton = findViewById(R.id.postButton);
        postsListView = findViewById(R.id.postsListView);

        // Set click listener for the Post button
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postText = postEditText.getText().toString().trim();
                if (!postText.isEmpty()) {
                    // Create a new Post object using the sample session user's data
                    Post newPost = new Post(currentUser.getUserName(), currentUser.getUserPfp(), postText, "", currentUser.getUserId());

                    // Update the posts in the database
                    database.getPostsDB().addPost(newPost);

                    // Retrieve all posts from the database
                    List<Post> allPosts = database.getPostsDB().getAllPosts();

                    // Update the ListView with the retrieved posts
                    ((PostAdapter) postsListView.getAdapter()).updatePosts(allPosts);

                    // Clear the EditText after posting
                    postEditText.getText().clear();
                } else {
                    // Show a toast message or take appropriate action for empty post
                    Toast.makeText(FeedActivity.this, "Post cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up the ListView with the PostAdapter
        PostAdapter adapter = new PostAdapter(this, new ArrayList<>(), currentUser, database);
        postsListView.setAdapter(adapter);

        // Retrieve all posts from the database
        List<Post> allPosts = database.getPostsDB().getAllPosts();

        // Update the ListView with the retrieved posts
        adapter.updatePosts(allPosts);
    }
}
