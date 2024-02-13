package com.example.facebookapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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
import android.widget.ToggleButton;

import java.util.List;

public class PostAdapter extends BaseAdapter {

    private List<Post> postList;
    private LayoutInflater inflater;
    private Context context;
    private User currentUser;
    private DB database; // Reference to the database

    public PostAdapter(Context context, List<Post> postList, User currentUser, DB database) {
        this.context = context;
        this.postList = postList;
        this.currentUser = currentUser;
        this.inflater = LayoutInflater.from(context);
        this.database = database; // Initialize the reference to the database
    }

    public void updatePosts(List<Post> newPosts) {
        postList.clear();
        postList.addAll(newPosts);
        notifyDataSetChanged();
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

        // Get the current post
        Post currentPost = (Post) getItem(position);

        // Populate the views with post details
        TextView postContentTextView = view.findViewById(R.id.postContentTextView);
        ToggleButton likeButton = view.findViewById(R.id.likeButton);
        Button commentButton = view.findViewById(R.id.commentButton);
        Button postButton = view.findViewById(R.id.postButton);
        EditText commentEditText = view.findViewById(R.id.commentEditText);
        Button shareButton = view.findViewById(R.id.shareButton);

        postContentTextView.setText(currentPost.getContent());

        // Display the post image if available
        ImageView postImageView = view.findViewById(R.id.postImageView);
        String imageString = currentPost.getImg();
        Uri imageUri = Uri.parse(imageString);
        if (!imageString.isEmpty()) {
            postImageView.setVisibility(View.VISIBLE);
            postImageView.setImageURI(imageUri);
            Log.d("ImageUriDebug", "ImageUri: " + imageUri.toString());

        } else {
            postImageView.setVisibility(View.GONE);
        }

        // Add click listener to the delete icon
        ImageView deleteIcon = view.findViewById(R.id.deleteIcon);
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the current post from the database
                database.getPostsDB().deletePost(currentPost);

                // Update the adapter to reflect the changes
                updatePosts(database.getPostsDB().getAllPosts());
            }
        });

        // Add click listener to the edit icon
        ImageView editIcon = view.findViewById(R.id.editIcon);
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Provide a way for the user to edit the post content
                showEditDialog(currentPost);
            }
        });

        postContentTextView.setText(currentPost.getContent());

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

        // Set click listener for the Like button
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the post is liked or not
                if (!likeButton.isChecked()) {
                    // If not liked, increase the like count by 1
                    currentPost.incLikes();
                    likeButton.setText("Liked");
                    database.getPostsDB().incLikes(currentPost, 1);
                } else {
                    // If already liked, decrease the like count by 1
                    currentPost.decLikes();
                    likeButton.setText("Like");
                    database.getPostsDB().incLikes(currentPost, -1);
                }
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareContent();
            }
        });


        CommentAdapter commentAdapter;
        ListView commentListView = view.findViewById(R.id.commentListView);
        if (convertView == null) {
            commentAdapter = new CommentAdapter(context, currentPost.getComments(), database, currentPost);
            commentListView.setAdapter(commentAdapter);
        } else {
            // Use the existing commentAdapter
            commentAdapter = (CommentAdapter) commentListView.getAdapter();
        }


        // Set click listener for the Post button
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = commentEditText.getText().toString();

                // Check if the comment is not empty
                if (!commentText.isEmpty()) {
                    // Add the comment to the current post using the current user's data
                    Comment newComment = new Comment(currentUser.getUserId(), new Time(), false, commentText,
                            currentUser.getUserName(), currentUser.getUserPfp(), 1);

                    // Update the post in the database with the new comment
                    database.getPostsDB().getPostById(currentPost.getPostId()).addComment(currentUser, newComment.getContent());

                    // Update the CommentAdapter to reflect the new comment
                    commentAdapter.updateComments(database.getPostsDB().getPostById(currentPost.getPostId()).getComments());

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

    private void shareContent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Share post");

        Intent chooser = Intent.createChooser(shareIntent, "Share via");
        if (context.getPackageManager() != null) {
            context.startActivity(chooser);
        }
    }

    // Method to show a dialog for post editing
    private void showEditDialog(Post post) {
        // Create a dialog with an EditText for post editing
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Post");

        final EditText editPostEditText = new EditText(context);
        editPostEditText.setText(post.getContent());
        builder.setView(editPostEditText);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Update the post content and notify the adapter if the text is not empty
                String updatedPostText = editPostEditText.getText().toString().trim();

                if (!updatedPostText.isEmpty()) {
                    post.setContent(updatedPostText);
                    database.getPostsDB().editPost(post, updatedPostText, ""); // Update post in the database
                    updatePosts(database.getPostsDB().getAllPosts());
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

}

