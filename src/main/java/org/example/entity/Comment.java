package org.example.entity;

import org.example.id.ArticleId;
import org.example.id.CommentId;

public class Comment {
  private final CommentId commentID;
  private final String text;
  private final ArticleId articleID;

  public Comment(CommentId commentID, String text, ArticleId articleID) {
    this.commentID = commentID;
    this.text = text;
    this.articleID = articleID;
  }

  public CommentId getID() {
    return commentID;
  }

  @Override
  public String toString() {
    return "Comment{" + "commentID=" + commentID + ", text='" + text + '\'' + '}';
  }
}