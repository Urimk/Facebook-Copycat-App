package com.example.facebookapp;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private ImageView pfpPreview;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICKER = 2;
    private Uri pfpUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        EditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        EditText editTextDisplayName = findViewById(R.id.editTextDisplayName);
        Button buttonRegister = findViewById(R.id.buttonRegister);
        Button buttonUploadImg = findViewById(R.id.buttonUploadImage);
        pfpPreview = findViewById(R.id.pfpPreview);
        TextView errorUsername = findViewById(R.id.errorUsername);
        TextView errorPassword = findViewById(R.id.errorPassword);
        TextView errorConfirmPassword = findViewById(R.id.errorConfirmPassword);
        TextView errorDisplayName = findViewById(R.id.errorDisplayName);
        pfpUri = Uri.parse(""); // incase use


        // Set onClickListener for the Register button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate and show errors
                int errorFlag = 0;
                if (!isValidUsername(editTextUsername.getText().toString())) {
                    errorUsername.setText("Username must contain only letters and numbers and be shorter than 16 characters");
                    errorUsername.setVisibility(View.VISIBLE);
                    errorFlag = 1;
                } else {
                    errorUsername.setVisibility(View.GONE);
                }

                if (!isValidPassword(editTextPassword.getText().toString())) {
                    errorPassword.setText("Password must have an uppercase letter and a number and 8 digits or longer");
                    errorPassword.setVisibility(View.VISIBLE);
                    errorFlag = 1;
                } else {
                    errorPassword.setVisibility(View.GONE);
                }

                if (!doPasswordsMatch(
                        editTextPassword.getText().toString(),
                        editTextConfirmPassword.getText().toString())) {
                    errorConfirmPassword.setText("Passwords do not match");
                    errorConfirmPassword.setVisibility(View.VISIBLE);
                    errorFlag = 1;
                } else {
                    errorConfirmPassword.setVisibility(View.GONE);
                }

                if (!isValidDisplayName(editTextDisplayName.getText().toString())) {
                    errorDisplayName.setText("Display name must contain only letters and numbers and be shorter than 16 characters");
                    errorDisplayName.setVisibility(View.VISIBLE);
                    errorFlag = 1;
                } else {
                    errorDisplayName.setVisibility(View.GONE);
                }

                if (errorFlag == 0) {
                    User user;
                    if (pfpUri == null) {
                        user = new User(editTextUsername.getText().toString(), "",
                                editTextDisplayName.getText().toString(),
                                editTextPassword.getText().toString());
                    } else {
                        user = new User(editTextUsername.getText().toString(), pfpUri.toString(),
                                editTextDisplayName.getText().toString(),
                                editTextPassword.getText().toString());
                    }
                    int code = DB.getUsersDB().addUser(user);

                    if (code == UsersDB.REGISTRATION_FAILED) {
                        errorUsername.setText("A user with that username already exists");
                        errorUsername.setVisibility(View.VISIBLE);
                    }
                    else {
                        finish(); // user has registered- take him back to MainActivity
                    }
                }

            }
        });

        buttonUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageSourceDialog();
            }
        });
    }

    private boolean isValidUsername(String username) {
        // Username must contain only letters and numbers and be shorter than 16 characters
        return username.matches("^[a-zA-Z0-9]{1,16}$");
    }

    private boolean isValidPassword(String password) {
        // Password must have an uppercase letter and a number
        return password.matches(".*[A-Z].*") && password.matches(".*[0-9].*") && password.length() >= 8;
    }

    private boolean doPasswordsMatch(String password, String confirmPassword) {
        // Confirm password must match the password
        return password.equals(confirmPassword);
    }

    private boolean isValidDisplayName(String displayName) {
        // Display name must contain only letters and numbers and be shorter than 16 characters
        return displayName.matches("^[a-zA-Z0-9]{1,16}$");
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
            pfpUri = getOutputMediaFileUri();
            if (pfpUri != null) {
                //the file allocation succeeded
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pfpUri);
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
                pfpUri = data.getData();
                ContentResolver resolver = this.getContentResolver();
                resolver.takePersistableUriPermission(pfpUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // You can use the selectedImageUri as needed (e.g., display in ImageView)
                Toast.makeText(this, "Image selected from gallery", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // data is null here because the uri is decided before capture and passed to the intent
                // Handle image captured from camera
                // The Uri is already updated because it was passed to the capture intent as an allocated file
                Toast.makeText(this, "Image captured from camera", Toast.LENGTH_SHORT).show();
            }
            pfpPreview.setImageURI(pfpUri);
        }
    }

}
