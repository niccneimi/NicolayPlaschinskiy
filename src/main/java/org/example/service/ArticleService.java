package org.example.service;

import java.util.List;
import java.util.Set;
import org.example.id.ArticleId;
import org.example.ConnectionDataBaseException;
import org.example.TransactionManager;
import org.example.entity.Article;
import org.example.entity.Comment;
import org.example.repository.articleRepository.ArticleRepository;
import org.example.repository.articleRepository.exception.ArticleIDDuplicatedException;
import org.example.repository.articleRepository.exception.ArticleNotFoundException;
import org.example.service.exception.ArticleCreateException;
import org.example.service.exception.ArticleDeleteException;
import org.example.service.exception.ArticleFindException;
import org.example.service.exception.ArticleUpdateException;
import org.jdbi.v3.core.ConnectionException;

public class ArticleService {

  private final ArticleRepository articleRepository;
  private final TransactionManager transactionManager;

  public ArticleService(ArticleRepository articleRepository, TransactionManager transactionManager) {
    this.articleRepository = articleRepository;
    this.transactionManager = transactionManager;
  }

  public List<Article> getArticles() {
    return articleRepository.getArticles();
  }

  public ArticleId generateId() throws ConnectionDataBaseException {
    try {

      return articleRepository.generateID();
    } catch (ConnectionException e) {
      throw new ConnectionDataBaseException("Cannot connect to Data Base", e);
    }
  }

  public Article findByID(ArticleId ID) {
    try {
      return articleRepository.findByID(ID);
    } catch (ArticleNotFoundException e) {
      throw new ArticleFindException("Cannot find article with ID=" + ID, e);
    }
  }

  public ArticleId create(String name, Set<String> tags) {
    ArticleId articleID = articleRepository.generateID();
    Article article = new Article(articleID, name, tags, null, false);
    try {
      articleRepository.create(article);
    } catch (ArticleIDDuplicatedException e) {
      throw new ArticleCreateException("Cannot create article", e);
    }
    return articleID;
  }

  public void update(ArticleId articleID, String name, Set<String> tags, List<Comment> comments, boolean trending) {
    Article article = new Article(articleID, name, tags, comments, trending);
    try {
      articleRepository.update(article);
    } catch (ArticleNotFoundException e) {
      throw new ArticleUpdateException("Cannot update article with ID=" + articleID, e);
    }
  }

  public void delete(ArticleId ID) {
    try {
      articleRepository.delete(ID);
    } catch (ArticleNotFoundException e) {
      throw new ArticleDeleteException("Cannot delete article with ID=" + ID, e);
    }
  }

  public void createArticles(List<Article> articles) {
    transactionManager.useTransaction(() -> {
        for (Article article : articles) {
          try {
            articleRepository.create(article);
          } catch (ArticleIDDuplicatedException e) {
            throw new ArticleCreateException("Cannot create article", e);
          }
        }
      }
    );
  }
}