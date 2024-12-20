package org.example.repository.commentRepository;

import org.example.id.ArticleId;
import org.example.id.CommentId;
import org.example.entity.Comment;

public interface CommentRepository {
  CommentId generateID();

  Comment findByID(ArticleId articleID, CommentId commentID);

  void create(ArticleId articleID, Comment comment);

  void delete(ArticleId articleID, CommentId commentID);
}