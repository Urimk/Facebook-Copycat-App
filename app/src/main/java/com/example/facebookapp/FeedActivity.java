package com.example.facebookapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICKER = 2;

    private EditText postEditText;
    private ImageButton imageUploadButton;
    private Button postButton;
    private ListView postsListView;

    private User currentUser;  // Sample session user
    private DB database; // Reference to the database

    private Uri selectedImageUri;  // Use Uri instead of Bitmap

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
                    // Create a new Post object using the sample session user's data
                    Post newPost = new Post(currentUser.getUserName(), currentUser.getUserPfp(), postText, selectedImageUri.toString(), currentUser.getUserId());

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

    private void dispatchImagePickerIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_IMAGE_PICKER);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Take a Photo", "Upload from Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        dispatchTakePictureIntent();
                        break;
                    case 1:
                        dispatchImagePickerIntent();
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICKER && data != null) {
                // Handle image selected from gallery
                selectedImageUri = data.getData();
                // You can use the selectedImageUri as needed (e.g., display in ImageView)
                Toast.makeText(this, "Image selected from gallery", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                // Handle image captured from camera
                selectedImageUri = data.getData();
                // You can use the selectedImageUri as needed (e.g., display in ImageView)
                Toast.makeText(this, "Image captured from camera", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
