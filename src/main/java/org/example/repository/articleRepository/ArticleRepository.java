package org.example.repository.articleRepository;

import org.example.id.ArticleId;
import org.example.entity.Article;

import java.util.List;

public interface ArticleRepository {
  ArticleId generateID();

  List<Article> getArticles();

  Article findByID(ArticleId ID);

  void create(Article article);

  void update(Article article);

  void delete(ArticleId ID);
}