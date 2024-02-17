package com.example.facebookapp;
import org.json.JSONException;
import org.json.JSONObject;


public class Comment {
    private String authorName;
    private String authorPfp;
    private int authorId, commentId;
    private Time postTime;
    private boolean edited;
    private String content;

    public Comment(int authorId, Time postTime, boolean edited, String content, String authorName,
                   String authorPfp, int commentId) {
        this.authorId = authorId;
        this.postTime = new Time(postTime);
        this.edited = edited;
        this.content = new String(content);
        this.authorName = new String(authorName);
        this.authorPfp = new String(authorPfp);
        this.commentId = commentId;
    }

    public Comment(JSONObject comment) {
        try {
            this.authorId = comment.getInt("authorId");
            this.postTime = new Time(comment.getJSONObject("time"));
            this.edited = comment.getBoolean("edited");
            this.content = comment.getString("content");
            this.authorName = comment.getString("authorName");
            this.authorPfp = comment.getString("authorPfp");
            this.commentId = comment.getInt("commentId");

        }
        catch (JSONException exception) {
            this.authorId = -1;
            this.postTime = new Time();
            this.edited = false;
            this.content = "";
            this.authorName = "";
            this.authorPfp = "";
            this.commentId = -1;
        }
    }

    public int getAuthorId() {
        return authorId;
    }

    public Time getPostTime() {
        return postTime;
    }

    public boolean isEdited() {
        return edited;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorPfp() {
        return authorPfp;
    }

    public int getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) { this.content = content; }
}
