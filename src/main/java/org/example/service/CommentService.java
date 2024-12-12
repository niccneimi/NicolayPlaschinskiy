package org.example.service;

import org.example.id.ArticleId;
import org.example.id.CommentId;
import org.example.entity.Comment;
import org.example.repository.commentRepository.CommentRepository;
import org.example.repository.commentRepository.exception.CommentIDDuplicatedException;
import org.example.repository.commentRepository.exception.CommentNotFoundException;
import org.example.service.exception.CommentCreateException;
import org.example.service.exception.CommentDeleteException;
import org.example.service.exception.CommentFindException;

public class CommentService {
  private final CommentRepository commentRepository;

  public CommentService(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  public Comment findByID(ArticleId articleID, CommentId commentID) {
    try {
      return commentRepository.findByID(articleID, commentID);
    } catch (CommentNotFoundException e) {
      throw new CommentFindException("Cannot find comment with ID=" + commentID, e);
    }
  }

  public CommentId create(ArticleId articleID, String text) {
    CommentId commentID = commentRepository.generateID();
    Comment comment = new Comment(commentID, text, articleID);
    try {
      commentRepository.create(articleID, comment);
    } catch (CommentIDDuplicatedException e) {
      throw new CommentCreateException("Cannot create message", e);
    }
    return commentID;
  }

  public void delete(ArticleId articleID, CommentId commentID) {
    try {
      commentRepository.delete(articleID, commentID);
    } catch (CommentNotFoundException e) {
      throw new CommentDeleteException("Cannot delete comment with ID=" + commentID, e);
    }
  }
}