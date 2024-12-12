package org.example.repository.articleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.example.id.ArticleId;
import org.example.entity.Article;
import org.example.entity.Comment;
import org.example.repository.articleRepository.exception.ArticleIDDuplicatedException;
import org.example.repository.articleRepository.exception.ArticleNotFoundException;

public class InMemoryArticleRepository implements ArticleRepository {

  private final AtomicLong nextID = new AtomicLong(0);
  private final Map<ArticleId, Article> articleMap = new ConcurrentHashMap<>();

  @Override
  public synchronized ArticleId generateID() {
    return new ArticleId(nextID.incrementAndGet());
  }

  @Override
  public List<Article> getArticles() {
    return new ArrayList<>(articleMap.values());
  }

  @Override
  public Article findByID(ArticleId ID) {
    Article article = articleMap.get(ID);
    if (article == null) {
      throw new ArticleNotFoundException("Cannot find article with ID=" + ID);
    }
    return article;
  }

  @Override
  public synchronized void create(Article article) {
    if (articleMap.get(article.getID()) != null) {
      throw new ArticleIDDuplicatedException(
          "Article with the given id already exists: " + article.getID());
    }
    articleMap.put(article.getID(), article);
  }

  @Override
  public synchronized void update(Article article) {
    if (articleMap.get(article.getID()) == null) {
      throw new ArticleNotFoundException("Cannot find article with ID=" + article.getID());
    }
    List<Comment> comments = articleMap.get(article.getID()).getComments();
    article.setComments(comments);
    articleMap.put(article.getID(), article);
  }

  @Override
  public void delete(ArticleId ID) {
    if (articleMap.remove(ID) == null) {
      throw new ArticleNotFoundException("Cannot find article with ID=" + ID);
    }
  }
}