package com.example.facebookapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICKER = 2;

    private EditText postEditText;
    private ImageButton imageUploadButton;
    private Button postButton;
    private ListView postsListView;

    private User currentUser;  // Sample session user

    private Uri selectedImageUri;  // Use Uri instead of Bitmap

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            String username = intent.getStringExtra("username");
            currentUser = DB.getUsersDB().getUserByUserName(username);
            setContentView(R.layout.activity_feed);
        }
        else {
            setContentView(R.layout.unauthorized_feed);
            return;
        }


        // Initialize UI components
        postEditText = findViewById(R.id.postEditText);
        imageUploadButton = findViewById(R.id.imageUploadButton);
        postButton = findViewById(R.id.postButton);
        postsListView = findViewById(R.id.postsListView);
        TextView usernameTextView = findViewById(R.id.usernameTextView);
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (currentUser.getUserName() != null) {
            usernameTextView.setText(currentUser.getUserNick());
        }

        // Inside FeedActivity, after initializing UI components
        ImageView profileImageView = findViewById(R.id.profileImageView);

        // Set the profile image URI if available
        String profileImageUriString = currentUser.getUserPfp();
        if (!profileImageUriString.isEmpty()) {
            Uri profileImageUri = Uri.parse(profileImageUriString);

            profileImageView.setImageURI(profileImageUri);

        } else {
            // Set a default image or handle the case when there is no profile image
            profileImageView.setImageResource(R.drawable.default_profile_pic);
        }

        // Set click listener for the Image Upload button
        imageUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceDialog();
            }
        });

        // Set click listener for the Post button
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postText = postEditText.getText().toString().trim();
                if (!postText.isEmpty()) {
                    // Get the image URI if available
                    String imageUriString = (selectedImageUri != null) ? selectedImageUri.toString() : "";

                    // Create a new Post object using the sample session user's data
                    Post newPost = new Post(currentUser.getUserName(), currentUser.getUserPfp(), postText, imageUriString, currentUser.getUserId());

                    // Update the posts in the database
                    DB.getPostsDB().addPost(newPost);

                    // Retrieve all posts from the database
                    List<Post> allPosts = DB.getPostsDB().getAllPosts();

                    // Update the ListView with the retrieved posts
                    ((PostAdapter) postsListView.getAdapter()).updatePosts(allPosts);

                    // Clear the EditText and reset the selectedImageUri after posting
                    postEditText.getText().clear();
                    selectedImageUri = null;
                } else {
                    // Show a toast message or take appropriate action for empty post
                    Toast.makeText(FeedActivity.this, "Post cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up the ListView with the PostAdapter
        PostAdapter adapter = new PostAdapter(this, new ArrayList<>(), currentUser);
        postsListView.setAdapter(adapter);

        // Retrieve all posts from the database
        List<Post> allPosts = DB.getPostsDB().getAllPosts();

        // Update the ListView with the retrieved posts
        adapter.updatePosts(allPosts);
        adapter.notifyDataSetChanged();
    }

    private void showImageSourceDialog() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                ContentResolver resolver = this.getContentResolver();
                resolver.takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
