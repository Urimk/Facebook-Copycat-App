package com.example.facebookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;

import java.util.List;

public class CommentAdapter extends BaseAdapter {

    private List<Comment> commentList;
    private LayoutInflater inflater;
    private Context context;
    private DB database; // Reference to the database
    private Post associatedPost; // Reference to the associated Post

    public CommentAdapter(Context context, List<Comment> commentList, DB database, Post associatedPost) {
        this.context = context;
        this.commentList = commentList;
        this.inflater = LayoutInflater.from(context);
        this.database = database; // Initialize the reference to the database
        this.associatedPost = associatedPost; // Initialize the reference to the associated Post
    }

    public void updateComments(List<Comment> newComments) {
        commentList.clear();
        commentList.addAll(newComments);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.comment_item, null);
        }

        // Get the current comment
        Comment currentComment = (Comment) getItem(position);

        // Populate the views with comment details
        TextView commentTextView = view.findViewById(R.id.commentContentTextView);
        TextView displayNameTextView = view.findViewById(R.id.displayNameTextView);
        TextView dateTextView = view.findViewById(R.id.dateTextView);
        ImageView deleteIcon = view.findViewById(R.id.deleteIcon);
        ImageView editIcon = view.findViewById(R.id.editIcon);

        commentTextView.setText(currentComment.getContent());
        displayNameTextView.setText(currentComment.getAuthorName());
        dateTextView.setText(currentComment.getPostTime().toString()); // Use appropriate method to get the date

        // Add click listener to the delete icon
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the current comment from the database
                database.getPostsDB().removeComment(associatedPost, currentComment);

                // Update the adapter to reflect the changes
                updateComments(database.getPostsDB().getPostById(associatedPost.getPostId()).getComments());

                // Optionally, you can show a toast message or perform other actions
                Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
            }
        });

        // Add click listener to the edit icon
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show an edit dialog for the comment
                showEditDialog(currentComment);
            }
        });

        return view;
    }

    // Method to show a dialog for comment editing
    private void showEditDialog(Comment comment) {
        // Create a dialog with an EditText for comment editing
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Comment");

        final EditText editCommentEditText = new EditText(context);
        editCommentEditText.setText(comment.getContent());
        builder.setView(editCommentEditText);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Update the comment text only if it's not empty
                String updatedCommentText = editCommentEditText.getText().toString().trim();
                if (!updatedCommentText.isEmpty()) {
                    comment.setContent(updatedCommentText);
                    notifyDataSetChanged();

                    // Update the database to reflect the comment edit
                    database.getPostsDB().editComment(associatedPost, comment);
                } else {
                    // Show a toast message or take appropriate action for empty comment
                    Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
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
}

