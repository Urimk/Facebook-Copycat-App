package com.example.facebookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class PostAdapter extends BaseAdapter {

    private List<FeedActivity.PostItem> postList;
    private LayoutInflater inflater;
    private Context context; // Add this variable

    public PostAdapter(Context context, List<FeedActivity.PostItem> postList) {
        this.context = context;
        this.postList = postList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return postList.size();
    }

    @Override
    public Object getItem(int position) {
        return postList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.post_item, null);
        }

        // Get the current post item
        FeedActivity.PostItem currentPost = (FeedActivity.PostItem) getItem(position);

        // Populate the views with post details
        TextView postContentTextView = view.findViewById(R.id.postContentTextView);
        ImageView postImageView = view.findViewById(R.id.postImageView);
        TextView dateTextView = view.findViewById(R.id.dateTextView);
        Button likeButton = view.findViewById(R.id.likeButton);
        Button shareButton = view.findViewById(R.id.shareButton);
        Button commentButton = view.findViewById(R.id.commentButton);
        TextView commentsTextView = view.findViewById(R.id.commentsTextView);

        postContentTextView.setText(currentPost.getText());
        commentsTextView.setText(buildCommentsText(currentPost.getComments()));
        // Set other details as needed...

        // Set a click listener for the Comment button
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a dialog or navigate to a new activity to add a comment
                // You can implement this based on your preference
                // For simplicity, I'm showing a toast here
                Toast.makeText(context, "Add your comment functionality here", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private String buildCommentsText(List<FeedActivity.CommentItem> comments) {
        StringBuilder commentsText = new StringBuilder();
        for (FeedActivity.CommentItem comment : comments) {
            commentsText.append(comment.getCommentText()).append("\n");
        }
        return commentsText.toString().trim();
    }
}
