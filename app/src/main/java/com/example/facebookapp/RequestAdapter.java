package com.example.facebookapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.facebookapp.callbacks.AcceptFriendCallback;

import java.util.ArrayList;
import java.util.List;

public class RequestAdapter extends ArrayAdapter<FriendRequest> implements AcceptFriendCallback {

    private Context mContext;
    private List<FriendRequest> mRequestList;
    private int userId;

    public RequestAdapter(Context context, List<FriendRequest> requestList, int userId) {
        super(context, 0, requestList);
        mContext = context;
        mRequestList = requestList;
        this.userId = userId;
    }

    public void changeRequests(List<FriendRequest> requests) {
        this.mRequestList = requests;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(mContext).inflate(
                    R.layout.request_item, parent, false);
        }

        // Get the current request item
        FriendRequest currentItem = mRequestList.get(position);

        // Find views in the layout
        ImageView profileImageView = listItemView.findViewById(R.id.profileImageView);
        TextView usernameTextView = listItemView.findViewById(R.id.usernameTextView);
        Button confirmButton = listItemView.findViewById(R.id.confirmButton);

        // Set data to views
        Bitmap friendImg = ImageUtils.decodeImageFromBase64(currentItem.getUserPfp());
        profileImageView.setImageBitmap(friendImg);
        usernameTextView.setText(currentItem.getUserNick());
        AcceptFriendCallback callback = this;

        // Set click listener for confirm button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle confirm button click
                // You can implement your logic here
                FriendApi friendApi = new FriendApi();
                friendApi.acceptFriend(userId, mRequestList.get(position).getUserId(), callback);
            }
        });

        return listItemView;
    }

    @Override
    public void onSuccess(int userId) {
        for (int i = 0; i < mRequestList.size(); i++) {
            if (mRequestList.get(i).getUserId() == userId) {
                mRequestList.remove(i);
                break;
            }
        }
        notifyDataSetChanged();

    }

    @Override
    public void onFailure() {

    }
}
