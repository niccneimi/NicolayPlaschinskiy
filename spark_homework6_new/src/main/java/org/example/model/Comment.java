package org.example.model;

public final class Comment {
    private final CommentId id;
    private final ArticleId articleId;
    private final String text;

    public Comment(CommentId id, ArticleId articleId, String text) {
        this.id = id;
        this.articleId = articleId;
        this.text = text;
    }

    public CommentId getId() { return id; }
    public ArticleId getArticleId() { return articleId; }
    public String getText() { return text; }
}
