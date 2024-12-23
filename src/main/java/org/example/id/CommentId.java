package org.example.id;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;


public class CommentId {
  @JsonProperty("CommentID")
  private final long ID;

  public CommentId(long ID) {
    this.ID = ID;
  }

  @Override
  public String toString() {
    return "" + ID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CommentId commentId)) return false;
    return ID == commentId.ID;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(ID);
  }

  public long getValue() {
    return ID;
  }
}