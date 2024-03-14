package com.example.facebookapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
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
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FeedActivity extends AppCompatActivity {

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
        ToggleButton darkModeToggle = findViewById((R.id.darkModeToggle));
        ImageButton friendRequestsButton = findViewById(R.id.friendRequestsButton);

        darkModeToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FeedActivity.this);
                boolean currentMode = preferences.getBoolean("dark_mode_enabled", false);

                // Toggle the theme
                if (currentMode) {
                    setTheme(R.style.LightTheme);
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    setTheme(R.style.DarkTheme);
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }

                // Save the updated mode to SharedPreferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("dark_mode_enabled", !currentMode);
                editor.apply();

                recreate(); // Recreate the activity to apply the new theme
            }
        });

        // Set click listener for the Friend Requests button
        friendRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the RequestsActivity
                Intent intent = new Intent(FeedActivity.this, RequestsActivity.class);
                startActivity(intent);
            }
        });

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

    private void dispatchImagePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_IMAGE_PICKER);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            selectedImageUri = getOutputMediaFileUri();
            if (selectedImageUri!= null) {
                //the file allocation succeeded
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // Helper method to create a file Uri for saving the captured image in app
    private Uri getOutputMediaFileUri() {
        // Get the directory for the users public pictures directory.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyAppImages");

        // Create the storage directory if it does not exist.
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.e("MyApp", "Failed to create directory");
            return null; // Return null if the directory creation failed
        }

        // Create a media file name based on the current time
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        // Return the Uri for the file
        return FileProvider.getUriForFile(this, "com.example.facebookapp.fileprovider", mediaFile);
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
                ContentResolver resolver = this.getContentResolver();
                resolver.takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // You can use the selectedImageUri as needed (e.g., display in ImageView)
                Toast.makeText(this, "Image selected from gallery", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // data is null here because the uri is decided before capture and passed to the intent
                // Handle image captured from camera
                // The Uri is already updated because it was passed to the capture intent as an allocated file
                Toast.makeText(this, "Image captured from camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
