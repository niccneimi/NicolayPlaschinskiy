package org.example.service.exception;

import org.example.repository.articleRepository.exception.ArticleNotFoundException;

public class ArticleDeleteException extends RuntimeException {
  public ArticleDeleteException(String message) {
    super(message);
  }

  public ArticleDeleteException(String message, ArticleNotFoundException cause) {
    super(message, cause);
  }
}