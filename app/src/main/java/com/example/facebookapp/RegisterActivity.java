package com.example.facebookapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        EditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        EditText editTextDisplayName = findViewById(R.id.editTextDisplayName);
        Button buttonRegister = findViewById(R.id.buttonRegister);
        TextView errorUsername = findViewById(R.id.errorUsername);
        TextView errorPassword = findViewById(R.id.errorPassword);
        TextView errorConfirmPassword = findViewById(R.id.errorConfirmPassword);
        TextView errorDisplayName = findViewById(R.id.errorDisplayName);


        // Set onClickListener for the Register button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate and show errors
                if (!isValidUsername(editTextUsername.getText().toString())) {
                    errorUsername.setText("Username must contain only letters and numbers and be shorter than 16 characters");
                    errorUsername.setVisibility(View.VISIBLE);
                } else {
                    errorUsername.setVisibility(View.GONE);
                }

                if (!isValidPassword(editTextPassword.getText().toString())) {
                    errorPassword.setText("Password must have an uppercase letter and a number");
                    errorPassword.setVisibility(View.VISIBLE);
                } else {
                    errorPassword.setVisibility(View.GONE);
                }

                if (!doPasswordsMatch(
                        editTextPassword.getText().toString(),
                        editTextConfirmPassword.getText().toString())) {
                    errorConfirmPassword.setText("Passwords do not match");
                    errorConfirmPassword.setVisibility(View.VISIBLE);
                } else {
                    errorConfirmPassword.setVisibility(View.GONE);
                }

                if (!isValidDisplayName(editTextDisplayName.getText().toString())) {
                    errorDisplayName.setText("Display name must contain only letters and numbers and be shorter than 16 characters");
                    errorDisplayName.setVisibility(View.VISIBLE);
                } else {
                    errorDisplayName.setVisibility(View.GONE);
                }

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

}
