package org.example.model;

import java.util.List;
import java.util.Set;

public final class Article {
    private final ArticleId id;
    private final String title;
    private final Set<String> tags;
    private final List<Comment> comments;

    public Article(ArticleId id, String title, Set<String> tags, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.comments = comments;
    }

    public ArticleId getId() { return id; }
    public String getTitle() { return title; }
    public Set<String> getTags() { return tags; }
    public List<Comment> getComments() { return comments; }

    public Article withTitle(String newTitle) {
        return new Article(this.id, newTitle, this.tags, this.comments);
    }

    public Article withComments(List<Comment> newComments) {
        return new Article(this.id, this.title, this.tags, newComments);
    }
}