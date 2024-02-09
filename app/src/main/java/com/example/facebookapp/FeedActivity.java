package com.example.facebookapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private EditText postEditText;
    private ImageButton imageUploadButton;
    private Button postButton;
    private ListView postsListView;

    private List<PostItem> postList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // Initialize UI components
        postEditText = findViewById(R.id.postEditText);
        imageUploadButton = findViewById(R.id.imageUploadButton);
        postButton = findViewById(R.id.postButton);
        postsListView = findViewById(R.id.postsListView);

        // Set click listener for the Post button
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new PostItem with the entered text
                String postText = postEditText.getText().toString();
                PostItem newPostItem = new PostItem(postText);

                // Add the new post to the list
                postList.add(newPostItem);

                // Update the ListView
                ((PostAdapter) postsListView.getAdapter()).notifyDataSetChanged();

                // Clear the EditText after posting
                postEditText.getText().clear();
            }
        });

        // Set up the ListView with the PostAdapter
        PostAdapter adapter = new PostAdapter(this, postList);
        postsListView.setAdapter(adapter);
    }


    // Inside FeedActivity.java
    public static class CommentItem {
        private String commentText;
        private String displayName;
        private String date;

        public CommentItem(String commentText, String displayName, String date) {
            this.commentText = commentText;
            this.displayName = displayName;
            this.date = date;
        }

        public String getCommentText() {
            return commentText;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDate() {
            return date;
        }
    }



    // Define a data class for a PostItem
    public static class PostItem {
        private String text;
        private List<CommentItem> comments;

        public PostItem(String text) {
            this.text = text;
            this.comments = new ArrayList<>();
        }

        public String getText() {
            return text;
        }

        public List<CommentItem> getComments() {
            return comments;
        }

        public void addComment(String commentText, String displayName, String date) {
            CommentItem commentItem = new CommentItem(commentText, displayName, date);
            comments.add(commentItem);
        }
    }
}
