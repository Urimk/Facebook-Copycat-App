package com.example.facebookapp;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Post {
    @PrimaryKey(autoGenerate = true)
    private int postId;
    private int likes = 0, shares = 0, authorId;
    private String authorName, content;
    private String authorPfp, img;
    private String postTime;
    @Ignore
    private List<Comment> comments;
    private boolean edited;

    private static int commentIdCounter = 0;


    public Post(String authorName, String authorPfp, String postTime, int likes, int shares,
                int postId, List<Comment> comments, String content, String img, boolean edited,
                int authorId) {
        this.postId = postId;
        this.likes = likes;
        this.shares = shares;
        this.authorName = new String(authorName);
        this.content = new String(content);
        this.authorPfp = new String(authorPfp);
        this.img = new String(img);
        this.postTime = postTime + "";
        this.comments = new ArrayList<>(comments);
        this.edited = edited;
        this.authorId = authorId;
    }

    public Post(String authorName, String authorPfp, String content, String img, int authorId) {
        // when a new post is made
        // if a post doesn't have an image pass ""
        this.authorName = new String(authorName);
        this.content = new String(content);
        this.authorPfp = new String(authorPfp);
        this.img = new String(img);
        this.postTime = "now";
        this.comments = new ArrayList<>();
        this.edited = false;
        this.authorId = authorId;
    }

    public Post(JSONObject jsonPost) {
        try {
            this.postId = jsonPost.getInt("postId");
            this.likes = jsonPost.getInt("likes");
            this.shares = jsonPost.getInt("shares");
            this.authorId = jsonPost.getInt("authorId");
            this.postTime = jsonPost.getString("time");
            this.edited = jsonPost.getBoolean("edited");
            this.content = jsonPost.getString("content");
            this.authorName = jsonPost.getString("authorName");
            this.authorPfp = jsonPost.getString("authorPfp");
            this.img = jsonPost.getString("img");
            this.comments = new ArrayList<>();
            JSONArray jsonComments = jsonPost.getJSONArray("comments");
            for (int i = 0; i < jsonComments.length(); i ++) {
                // read all the comments from the comments json array
                comments.add(new Comment(jsonComments.getJSONObject(i)));
            }

        }
        catch (JSONException exception) {
            this.postId = -1;
            this.likes = 0;
            this.shares = 0;
            this.authorId = -1;
            this.postTime = "now";
            this.edited = false;
            this.content = "";
            this.authorName = "";
            this.authorPfp = "";
            this.img = "";
            this.comments = new ArrayList<>();
        }
    }

    public int getPostId() {
        return postId;
    }

    public int getLikes() {
        return likes;
    }

    public int getShares() {
        return shares;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) { this.content = content; }

    public String getAuthorPfp() {
        return authorPfp;
    }

    public void incLikes() { likes++; }

    public void decLikes() { likes--; }

    public String getImg() {
        return img;
    }

    public String getPostTime() {
        return postTime;
    }

    public List<Comment> getComments() {
        return new ArrayList<>(this.comments);
    }

    public void addComment(Comment comment) {
        int newCommentId = commentIdCounter++; // Increment the counter to get a unique comment ID
        Comment newComment = new Comment(comment, newCommentId);
        comments.add(newComment);
    }

    public int getAuthorId() {
        return authorId;
    }
    public boolean isEdited() {
        return edited;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setAuthorPfp(String authorPfp) {
        this.authorPfp = authorPfp;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }
}
