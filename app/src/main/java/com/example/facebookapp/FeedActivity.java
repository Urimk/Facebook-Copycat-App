package com.example.facebookapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.facebookapp.callbacks.AddFriendCallback;
import com.example.facebookapp.callbacks.GetFriendsCallback;
import com.example.facebookapp.callbacks.GetUserCallback;
import com.example.facebookapp.returntypes.UserIdResponse;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FeedActivity extends AppCompatActivity implements GetUserCallback, AddFriendCallback, GetFriendsCallback {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICKER = 2;

    private EditText postEditText;
    private ImageButton imageUploadButton;
    private Button postButton;
    private ListView postsListView;
    private TextView usernameTextView;

    private User currentUser;  // Sample session user
    private User wallUser;

    private Uri selectedImageUri;  // Use Uri instead of Bitmap
    private String basedImage;
    private PostAdapter adapter;
    private List<Post> postList;
    private SwipeRefreshLayout refreshLayout;
    private ImageView wallPfp;
    private TextView wallNick;
    private int wallId = -1;
    private ImageButton addFriendBtn;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PostsViewModel viewModel;
        Intent intent = getIntent();
        UserApi userApi = new UserApi();
        if (intent != null) {
            String username = intent.getStringExtra("username");
            SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", this.MODE_PRIVATE);
            String jwt = sharedPreferences.getString("jwt", null);
            String[] jwtParts = jwt.split("\\.");
            String payload = jwtParts[1];
            byte[] decodedBytes = Base64.decode(payload, Base64.DEFAULT);
            String decodedPayLoad = new String(decodedBytes);
            Gson gson = new Gson();
            UserIdResponse response = gson.fromJson(decodedPayLoad, UserIdResponse.class);
            int userId = response.getUserId();

            userApi.getUser(userId,false, this);
            setContentView(R.layout.activity_feed);
        }
        else {
            setContentView(R.layout.unauthorized_feed);
            return;
        }

        PostViewModelFactory factory;
        // Initialize UI components
        postEditText = findViewById(R.id.postEditText);
        imageUploadButton = findViewById(R.id.imageUploadButton);
        postButton = findViewById(R.id.postButton);
        postsListView = findViewById(R.id.postsListView);
        usernameTextView = findViewById(R.id.usernameTextView);
        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        Button logoutButton = findViewById(R.id.logoutButton);
        ToggleButton darkModeToggle = findViewById((R.id.darkModeToggle));
        if (intent.hasExtra("wallId")) {
            refreshLayout.setVisibility(View.GONE);
            // feed is a wall, hide search and new post
            RelativeLayout searchLayout = findViewById(R.id.topMenuLayout);
            RelativeLayout writePostLayout = findViewById(R.id.writePostLayout);
            RelativeLayout wallInfoLayout = findViewById(R.id.wallInfoLayout);
            wallId = intent.getIntExtra("wallId", -1);
            searchLayout.setVisibility(View.GONE);
            writePostLayout.setVisibility(View.GONE);
            wallInfoLayout.setVisibility(View.VISIBLE);
            // set posts layout below the new wall info layout
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) refreshLayout.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.wallInfoLayout);
            refreshLayout.setLayoutParams(params);
            addFriendBtn = findViewById(R.id.addFriend);
            FriendApi friendApi = new FriendApi();
            AddFriendCallback callback = this;
            addFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    friendApi.add(wallId, callback);

                }
            });
            wallPfp = findViewById(R.id.wallPfp);
            wallNick = findViewById(R.id.wallUsernick);
            userApi.getUser(wallId, true, this);
            factory = new PostViewModelFactory(userId, wallId);
        }
        else {
            factory = new PostViewModelFactory(userId);
        }
        viewModel = new ViewModelProvider(this, factory).get(PostsViewModel.class);
        viewModel.reload();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.reload();
            }
        });
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
                intent.putExtra("userId", currentUser.getUserId());
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



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
                    Post newPost = new Post(currentUser.getUserName(), currentUser.getUserPfp(), postText, basedImage, currentUser.getUserId());
                    viewModel.add(newPost);

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
        adapter = new PostAdapter(this, new ArrayList<>(), currentUser, this);
        postsListView.setAdapter(adapter);

        // Retrieve all posts from the database
        postList = new ArrayList<Post>();
        viewModel.get().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                postList = posts;
                // Update the ListView with the retrieved posts
                adapter.updatePosts(postList);
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
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
                basedImage = ImageUtils.encodeImageToBase64(resolver, selectedImageUri);
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

    private void showUserInfo() {
        if (currentUser.getUserName() != null) {
            usernameTextView.setText(currentUser.getUserNick());
        }

        // Inside FeedActivity, after initializing UI components
        ImageView profileImageView = findViewById(R.id.profileImageView);

        // Set the profile image URI if available
        String profileImageUriString = currentUser.getUserPfp();
        if (!profileImageUriString.isEmpty()) {
            profileImageView.setImageBitmap(ImageUtils.decodeImageFromBase64(currentUser.getUserPfp()));

        } else {
            // Set a default image or handle the case when there is no profile image
            profileImageView.setImageResource(R.drawable.default_profile_pic);
        }
    }

    @Override
    public void onSuccess(User user, boolean isWallUser) {
        if (!isWallUser) {
            currentUser = user;
            showUserInfo();
            adapter = new PostAdapter(this, new ArrayList<>(), currentUser, this);
            postsListView.setAdapter(adapter);
            FriendApi friendApi = new FriendApi();
            friendApi.getFriends(currentUser.getUserId(), this);
        }
        else {
            wallUser = user;
            Bitmap img = ImageUtils.decodeImageFromBase64(wallUser.getUserPfp());
            wallPfp.setImageBitmap(img);
            wallNick.setText(wallUser.getUserNick());
        }
    }

    @Override
    public void onSuccess() {
        addFriendBtn.setBackgroundColor(Color.BLUE);
    }

    @Override
    public void onSuccess(List<FriendRequest> friendsList) {
        for (int i = 0; i < friendsList.size(); i++) {
            if (friendsList.get(i).getUserId() == wallId || (currentUser.getUserId() == wallId)) {
                // connected and wall are friends show post
                refreshLayout.setVisibility(View.VISIBLE);
                addFriendBtn.setVisibility(View.GONE);
                return;
            }
        }
    }

    @Override
    public void onFailure() {

    }
}
