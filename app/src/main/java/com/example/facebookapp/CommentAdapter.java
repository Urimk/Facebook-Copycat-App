package com.example.facebookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
        TextView commentTextView = view.findViewById(R.id.commentContentTextView);  // Corrected ID
        TextView displayNameTextView = view.findViewById(R.id.displayNameTextView);
        TextView dateTextView = view.findViewById(R.id.dateTextView);

        commentTextView.setText(currentComment.getCommentText());
        displayNameTextView.setText(currentComment.getDisplayName());
        dateTextView.setText(currentComment.getDate());

        return view;
    }
}
