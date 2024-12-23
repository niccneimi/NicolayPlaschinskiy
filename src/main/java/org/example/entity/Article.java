package org.example.entity;

import java.util.List;
import java.util.Set;
import org.example.id.ArticleId;


public class Article {
  private final ArticleId ID;
  private final String name;
  private final Set<String> tags;
  private final List<Comment> comments;
  private final boolean trending;

  public Article(ArticleId ID, String name, Set<String> tags, List<Comment> comments, boolean trending) {
    this.ID = ID;
    this.name = name;
    this.tags = tags;
    this.comments = comments;
    this.trending = trending;
  }

  public ArticleId getID() {
    return ID;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public String getName() {
    return name;
  }

  public String getTags() {
    return tags.toString();
  }

  public Set<String> getSetTags() {
    return tags;
  }

  public String getCommentsAPI() {
    return comments.toString();
  }

  public void setComments(List<Comment> comments) {
    this.comments.addAll(comments);
  }

  public boolean getTrending() {
    return trending;
  }

  @Override
  public String toString() {
    return "Article{"
        + "ID="
        + ID
        + ", name='"
        + name
        + '\''
        + ", tags="
        + tags
        + ", comments="
        + comments
        + '}';
  }
}