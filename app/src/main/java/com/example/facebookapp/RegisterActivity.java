package com.example.facebookapp;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 1;
    private ImageView pfpPreview;
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
                    errorPassword.setText("Password must have an uppercase letter and a number");
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
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    private boolean isValidUsername(String username) {
        // Username must contain only letters and numbers and be shorter than 16 characters
        return username.matches("^[a-zA-Z0-9]{1,16}$");
    }

    private boolean isValidPassword(String password) {
        // Password must have an uppercase letter and a number
        return password.matches(".*[A-Z].*") && password.matches(".*[0-9].*");
    }

    private boolean doPasswordsMatch(String password, String confirmPassword) {
        // Confirm password must match the password
        return password.equals(confirmPassword);
    }

    private boolean isValidDisplayName(String displayName) {
        // Display name must contain only letters and numbers and be shorter than 16 characters
        return displayName.matches("^[a-zA-Z0-9]{1,16}$");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                pfpUri = data.getData();
                pfpPreview.setImageURI(pfpUri);
                ContentResolver resolver = this.getContentResolver();
                resolver.takePersistableUriPermission(pfpUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            }
        }
    }
}
