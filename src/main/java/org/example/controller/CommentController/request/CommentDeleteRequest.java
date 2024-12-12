package org.example.controller.CommentController.request;

import org.example.id.ArticleId;
import org.example.id.CommentId;

public record CommentDeleteRequest(ArticleId articleID, CommentId commentID) {}