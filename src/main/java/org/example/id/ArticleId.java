package org.example.id;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class ArticleId {
  @JsonProperty("ArticleID")
  private final long ID;

  public ArticleId(long ID) {
    this.ID = ID;
  }

  @Override
  public String toString() {
    return "" + ID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ArticleId articleId)) return false;
    return ID == articleId.ID;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(ID);
  }

  public long getValue() {
    return ID;
  }
}