package org.example.controller.CommentController.request;

import org.example.id.ArticleId;

public record CommentCreateRequest(ArticleId articleID, String text) {}