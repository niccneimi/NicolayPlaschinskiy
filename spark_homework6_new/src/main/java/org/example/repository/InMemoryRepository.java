package org.example.repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.example.model.*;

public class InMemoryRepository {
    private final Map<ArticleId, Article> articles = new ConcurrentHashMap<>();
    private long articleCounter = 0;
    private long commentCounter = 0;

    public synchronized ArticleId generateArticleId() {
        return new ArticleId(++articleCounter);
    }

    public synchronized CommentId generateCommentId() {
        return new CommentId(++commentCounter);
    }

    public void addArticle(Article article) {
        articles.put(article.getId(), article);
    }

    public Optional<Article> findArticleById(ArticleId id) {
        return Optional.ofNullable(articles.get(id));
    }

    public List<Article> findAllArticles() {
        return new ArrayList<>(articles.values());
    }

    public void updateArticle(Article article) {
        articles.put(article.getId(), article);
    }

    public void deleteArticle(ArticleId id) {
        articles.remove(id);
    }

    public void addCommentToArticle(ArticleId articleId, Comment comment) {
        articles.computeIfPresent(articleId, (id, article) -> 
            article.withComments(new ArrayList<>(article.getComments()) {{
                add(comment);
            }})
        );
    }

    public void deleteCommentFromArticle(ArticleId articleId, CommentId commentId) {
        articles.computeIfPresent(articleId, (id, article) -> 
            article.withComments(new ArrayList<>(article.getComments()) {{
                removeIf(comment -> comment.getId().equals(commentId));
            }})
        );
    }
}
