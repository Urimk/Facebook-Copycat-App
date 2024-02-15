package com.example.facebookapp;

import android.content.Context;
import android.content.res.Resources;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PostsDB {
    private List<Post> posts;
    private int nextNewPostId = 20;

   public PostsDB(int resourceId, Context context){
       this.posts = new ArrayList<>();
       Resources resources = context.getResources();
       JSONObject jsonObject = null;
       try (InputStream inputStream = resources.openRawResource(resourceId)) {
           Scanner scanner = new Scanner(inputStream);
           StringBuilder stringBuilder = new StringBuilder();
           while (scanner.hasNextLine()) {
               stringBuilder.append(scanner.nextLine());
           }
           jsonObject = new JSONObject(stringBuilder.toString());
       }
       catch (Exception e) {
           e.printStackTrace();
       }
       if (jsonObject == null) {
           // no file found
           this.nextNewPostId = 20;
           return;
       }
       try {
           JSONArray jsonPosts = jsonObject.getJSONArray("posts");
           int maxId = 0;
           for (int i = 0; i < jsonPosts.length(); i++) {
               JSONObject jsonPost = jsonPosts.getJSONObject(i);
               Post post = new Post(jsonPost);
               this.posts.add(post);
               maxId = Math.max(maxId, post.getPostId());
           }
           this.nextNewPostId = maxId + 1;
       }
       catch (Exception e) {
           e.printStackTrace();
       }
   }

   public List<Post> getAllPosts() {
       // post has not setters so exposing a post doesn't matter
       // a copy of the list is made because changes to returned list shouldn't affect this class
       return new ArrayList<>(this.posts);
   }

   public Post getPostById(int id){
       for (Post post: this.posts) {
           if (post.getPostId() == id) {
               return post;
           }
       }
       return null; // post not found
   }

   public int addPost(Post post) {
       // construct a new post because posts are immutable and add the id decided by db
       Post newPost = new Post(post.getAuthorName(), post.getAuthorPfp(),
               post.getPostTime(), post.getLikes(), post.getShares(),
               this.nextNewPostId, post.getComments(), post.getContent(),
               post.getImg(), post.isEdited(), post.getAuthorId());
       this.nextNewPostId++;
       this.posts.add(newPost);
       return newPost.getPostId();
   }

   public void editPost(Post post, String content, String img) {
       // edit post with new content and img
       int i = 0;
       for(; i < posts.size(); i++) {
           if(posts.get(i).getPostId() == post.getPostId()){
               break;
           }
       }
       posts.remove(i);
       // construct a new post because posts are immutable
       Post editedPost = new Post(post.getAuthorName(), post.getAuthorPfp(), post.getPostTime(),
               post.getLikes(), post.getShares(), post.getPostId(), post.getComments(),
               content, img, true, post.getAuthorId());
       posts.add(editedPost);
   }

   public void deletePost(Post post) {
       // delete post by looking for id and removing it
       int i = 0;
       for (; i < this.posts.size(); i++) {
           if(this.posts.get(i).getPostId() == post.getPostId()) {
               break;
           }
       }
       this.posts.remove(i);
   }

   public void incLikes(Post post, int amount) {
       // increase likes on a post
       // construct a new post because posts are immutable
       Post incLikePost = new Post(post.getAuthorName(), post.getAuthorPfp(), post.getPostTime(),
               post.getLikes() + amount, post.getShares(), post.getPostId(), post.getComments(),
               post.getContent(), post.getImg(), post.isEdited(), post.getAuthorId());

       int i = 0;
       for (; i < this.posts.size(); i++) {
           if(this.posts.get(i).getPostId() == post.getPostId()) {
               break;
           }
       }
       this.posts.remove(i);
       this.posts.add(incLikePost);
   }

    public void removeComment(Post post, Comment comment) {
        int postId = post.getPostId();

        // Find the post index
        int postIndex = -1;
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getPostId() == postId) {
                postIndex = i;
                break;
            }
        }

        if (postIndex != -1) {
            // Create a copy of the comments list
            List<Comment> comments = new ArrayList<>(posts.get(postIndex).getComments());

            // Find the comment index in the copied list
            int commentIndex = -1;
            for (int i = 0; i < comments.size(); i++) {
                if (comments.get(i).getCommentId() == comment.getCommentId()) {
                    commentIndex = i;
                    break;
                }
            }

            if (commentIndex != -1) {
                // Remove the comment from the copied list
                comments.remove(commentIndex);

                // Create a new Post with the modified comments list
                Post editedPost = new Post(
                        post.getAuthorName(),
                        post.getAuthorPfp(),
                        post.getPostTime(),
                        post.getLikes(),
                        post.getShares(),
                        postId,
                        comments,
                        post.getContent(),
                        post.getImg(),
                        post.isEdited(),
                        post.getAuthorId()
                );

                // Update the existing post in the posts list
                posts.set(postIndex, editedPost);

                // Log the removal of a comment
            }
        }
    }


    public void editComment(Post post, Comment comment) {
       // edit comment of post
        List<Comment> comments = post.getComments();
        int i = 0;
        for (; i < comments.size(); i++) {
            if (comments.get(i).getCommentId() == comment.getCommentId()) {
                break;
            }
        }
        // replace the comment with the same id with the newer comment
        comments.remove(i);
        comments.add(comment);
        Post editedPost = new Post(post.getAuthorName(), post.getAuthorPfp(), post.getPostTime(),
                post.getLikes(), post.getShares(), post.getPostId(), comments,
                post.getContent(), post.getImg(), post.isEdited(), post.getAuthorId());
        i = 0;
        for (; i < posts.size(); i++) {
            if (posts.get(i).getPostId() == post.getPostId()) {
                break;
            }
        }
        posts.remove(i);
        // edited post has the edited comment
        posts.add(editedPost);
    }
}
