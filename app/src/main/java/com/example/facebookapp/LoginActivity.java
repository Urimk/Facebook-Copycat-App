package com.example.facebookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.facebookapp.callbacks.LoginUserCallBack;

public class LoginActivity extends AppCompatActivity implements LoginUserCallBack {

    private EditText usernameEditText, passwordEditText;
    private TextView incorrectInfo;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppDB.initialize(this);
        UserApi userApi = new UserApi();
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.buttonLogin);
        Button registerButton = findViewById(R.id.buttonRegister);
        incorrectInfo = findViewById(R.id.errorLogin);
        DB db = new DB(this); //ONLY ONCE IN THE ENTIRE APP
        sharedPreferences = this.getSharedPreferences("MyPrefs", this.MODE_PRIVATE);
        LoginUserCallBack callBack = this;


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User connectUser = new User(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
                userApi.loginUser(connectUser, callBack);
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

    @Override
    public void onSuccess(String jwt) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("jwt", jwt);
        Intent feedIntent = new Intent(LoginActivity.this, FeedActivity.class);
        Log.v("JWT: ", jwt);
        // put in the username to make sure user has gone through login activity
        feedIntent.putExtra("username", usernameEditText.getText().toString());
        incorrectInfo.setVisibility(View.INVISIBLE);
        startActivity(feedIntent);
        editor.apply();
    }

    @Override
    public void onFailure() {
        //login failed
        incorrectInfo.setVisibility(View.VISIBLE);
    }
}