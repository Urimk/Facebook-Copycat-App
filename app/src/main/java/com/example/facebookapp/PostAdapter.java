package com.example.facebookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class PostAdapter extends BaseAdapter {

    private List<FeedActivity.PostItem> postList;
    private LayoutInflater inflater;
    private Context context;

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
        Button postButton = view.findViewById(R.id.postButton);
        EditText commentEditText = view.findViewById(R.id.commentEditText);
        postContentTextView.setText(currentPost.getText());

        // Set a click listener for the Comment button
        commentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Toggle visibility of the commentEditText and postButton
                int commentVisibility = commentEditText.getVisibility();

                // If commentEditText is currently gone, make it visible along with the postButton
                if (commentVisibility == View.GONE) {
                    commentEditText.setVisibility(View.VISIBLE);
                    postButton.setVisibility(View.VISIBLE);
                } else {
                    // If commentEditText is currently visible, make it gone along with the postButton
                    commentEditText.setVisibility(View.GONE);
                    postButton.setVisibility(View.GONE);
                }
            }
        });

        CommentAdapter commentAdapter = new CommentAdapter(context, currentPost.getComments());
        ListView commentListView = view.findViewById(R.id.commentListView);
        commentListView.setAdapter(commentAdapter);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Extract the comment from the EditText
                String commentText = commentEditText.getText().toString();

                // Check if the comment is not empty
                if (!commentText.isEmpty()) {
                    // Add the comment to the current post item
                    currentPost.addComment(commentText, "User", "Just now");

                    // Update the CommentAdapter to reflect the new comment
                    commentAdapter.notifyDataSetChanged();

                    // Clear the EditText after posting
                    commentEditText.getText().clear();
                }
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
