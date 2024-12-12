package org.example.repository.commentRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.example.id.ArticleId;
import org.example.id.CommentId;
import org.example.entity.Comment;
import org.example.repository.articleRepository.ArticleRepository;
import org.example.repository.commentRepository.exception.CommentIDDuplicatedException;
import org.example.repository.commentRepository.exception.CommentNotFoundException;

public class InMemoryCommentRepository implements CommentRepository {

  private final AtomicLong nextID = new AtomicLong(0);
  private final ArticleRepository articleRepository;

  public InMemoryCommentRepository(ArticleRepository articleRepository) {
    this.articleRepository = articleRepository;
  }

  @Override
  public synchronized CommentId generateID() {
    return new CommentId(nextID.incrementAndGet());
  }

  @Override
  public Comment findByID(ArticleId articleID, CommentId commentID) {
    List<Comment> comments = articleRepository.findByID(articleID).getComments();
    for (int i = 0; i < comments.size(); i++) {
      if (comments.get(i).getID().equals(commentID)) {
        return comments.get(i);
      }
    }
    throw new CommentNotFoundException("Cannot find comment with ID=" + commentID);
  }

  @Override
  public synchronized void create(ArticleId articleID, Comment comment) {
    if (articleRepository.findByID(articleID).getComments().contains(comment)) {
      throw new CommentIDDuplicatedException(
          "Comment with the given id already exists: " + comment.getID());
    }
    articleRepository.findByID(articleID).getComments().add(comment);
  }

  @Override
  public void delete(ArticleId articleID, CommentId commentID) {
    Comment comment = findByID(articleID, commentID);
    articleRepository.findByID(articleID).getComments().remove(comment);
  }
  
}