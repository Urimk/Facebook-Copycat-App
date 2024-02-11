package com.example.facebookapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

        // Add click listener to the delete icon
        ImageView deleteIcon = view.findViewById(R.id.deleteIcon);
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the current post from the list
                postList.remove(position);

                // Update the adapter to reflect the changes
                notifyDataSetChanged();
            }
        });

        // Add click listener to the edit icon
        ImageView editIcon = view.findViewById(R.id.editIcon);
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Provide a way for the user to edit the post content
                showEditDialog(currentPost, position);
            }
        });

        postContentTextView.setText(currentPost.getText());

        // Set click listener for the Comment button
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

        // Set click listener for the Post button
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
                } else {
                    // Show a toast message or take appropriate action for empty comment
                    Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    // Method to show a dialog for post editing
    private void showEditDialog(FeedActivity.PostItem postItem, int position) {
        // Create a dialog with an EditText for post editing
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Post");

        final EditText editPostEditText = new EditText(context);
        editPostEditText.setText(postItem.getText());
        builder.setView(editPostEditText);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Update the post content and notify the adapter if the text is not empty
                String updatedPostText = editPostEditText.getText().toString().trim();
                if (!updatedPostText.isEmpty()) {
                    postItem.setText(updatedPostText);
                    notifyDataSetChanged();
                } else {
                    // Show a toast message or take appropriate action for empty post
                    Toast.makeText(context, "Post cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing if the user cancels
            }
        });

        builder.show();
    }

    private String buildCommentsText(List<FeedActivity.CommentItem> comments) {
        StringBuilder commentsText = new StringBuilder();
        for (FeedActivity.CommentItem comment : comments) {
            commentsText.append(comment.getCommentText()).append("\n");
        }
        return commentsText.toString().trim();
    }
}
