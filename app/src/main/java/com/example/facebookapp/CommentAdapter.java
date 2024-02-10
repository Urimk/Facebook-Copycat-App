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

    private List<FeedActivity.CommentItem> commentList;
    private LayoutInflater inflater;
    private Context context;

    public CommentAdapter(Context context, List<FeedActivity.CommentItem> commentList) {
        this.context = context;
        this.commentList = commentList;
        this.inflater = LayoutInflater.from(context);
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

        // Get the current comment item
        FeedActivity.CommentItem currentComment = (FeedActivity.CommentItem) getItem(position);

        // Populate the views with comment details
        TextView commentTextView = view.findViewById(R.id.commentContentTextView);
        TextView displayNameTextView = view.findViewById(R.id.displayNameTextView);
        TextView dateTextView = view.findViewById(R.id.dateTextView);
        ImageView deleteIcon = view.findViewById(R.id.deleteIcon);
        ImageView editIcon = view.findViewById(R.id.editIcon);

        commentTextView.setText(currentComment.getCommentText());
        displayNameTextView.setText(currentComment.getDisplayName());
        dateTextView.setText(currentComment.getDate());

        // Add click listener to the delete icon
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the current comment from the list
                commentList.remove(position);

                // Update the adapter to reflect the changes
                notifyDataSetChanged();

                // Optionally, you can show a toast message or perform other actions
                Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
            }
        });

        // Add click listener to the edit icon
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show an edit dialog for the comment
                showEditDialog(currentComment, position);
            }
        });

        return view;
    }

    // Method to show a dialog for comment editing
    private void showEditDialog(FeedActivity.CommentItem commentItem, int position) {
        // Create a dialog with an EditText for comment editing
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Comment");

        final EditText editCommentEditText = new EditText(context);
        editCommentEditText.setText(commentItem.getCommentText());
        builder.setView(editCommentEditText);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Update the comment text only if it's not empty
                String updatedCommentText = editCommentEditText.getText().toString().trim();
                if (!updatedCommentText.isEmpty()) {
                    commentItem.setCommentText(updatedCommentText);
                    notifyDataSetChanged();
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
