package com.example.facebookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private TextView incorrectInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.buttonLogin);
        Button registerButton = findViewById(R.id.buttonRegister);
        incorrectInfo = findViewById(R.id.errorLogin);
        DB db = new DB(this); //ONLY ONCE IN THE ENTIRE APP


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = DB.getUsersDB().connectUser(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
                if (user == null) {
                    //login failed
                    incorrectInfo.setVisibility(View.VISIBLE);

                    return;
                }
                Intent feedIntent = new Intent(LoginActivity.this, FeedActivity.class);
                // put in the username to make sure user has gone through login activity
                feedIntent.putExtra("username", usernameEditText.getText().toString());
                incorrectInfo.setVisibility(View.INVISIBLE);
                startActivity(feedIntent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);

            }
        });


    }
}