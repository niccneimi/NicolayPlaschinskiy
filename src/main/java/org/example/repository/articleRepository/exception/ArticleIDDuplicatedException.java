package org.example.repository.articleRepository.exception;

public class ArticleIDDuplicatedException extends RuntimeException {
  public ArticleIDDuplicatedException(String message) {
    super(message);
  }

  public ArticleIDDuplicatedException(String message, Throwable cause) {
    super(message, cause);
  }
}