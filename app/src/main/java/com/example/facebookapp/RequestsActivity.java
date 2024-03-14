package com.example.facebookapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RequestsActivity extends Activity {

    private ListView requestsListView;
    private ArrayList<String> requestsList;
    private ArrayAdapter<String> requestsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        requestsListView = findViewById(R.id.requestsListView);

        // Populate sample requests list
        populateRequestsList();

        // Set adapter for the requests list
        requestsAdapter = new ArrayAdapter<>(this, R.layout.request_item, requestsList);
        requestsListView.setAdapter(requestsAdapter);

        // Set click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close this activity and return to previous activity
            }
        });

        // Set click listener for list items
        requestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String request = requestsList.get(position);
                // Handle the click event for each request item (e.g., confirm request)
                confirmRequest(request);
            }
        });
    }

    // Method to populate sample requests list (Replace with your actual logic to load requests)
    private void populateRequestsList() {
        requestsList = new ArrayList<>();
        requestsList.add("Request 1");
        requestsList.add("Request 2");
        requestsList.add("Request 3");
        // Add more requests as needed
    }

    // Method to handle confirming a friend request (Replace with your actual logic)
    private void confirmRequest(String request) {
        // Remove the confirmed request from the list
        requestsList.remove(request);
        // Update the ListView
        requestsAdapter.notifyDataSetChanged();
        // Show confirmation message
        Toast.makeText(this, "Request confirmed: " + request, Toast.LENGTH_SHORT).show();
    }
}
