package org.example.controller.CommentController;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.id.ArticleId;
import org.example.id.CommentId;
import org.example.controller.Controller;
import org.example.controller.ErrorResponse;
import org.example.controller.CommentController.request.CommentCreateRequest;
import org.example.controller.CommentController.request.CommentDeleteRequest;
import org.example.controller.CommentController.response.CommentCreateResponse;
import org.example.service.ArticleService;
import org.example.service.CommentService;
import org.example.service.exception.ArticleFindException;
import org.example.service.exception.CommentCreateException;
import org.example.service.exception.CommentDeleteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

public class CommentController implements Controller {
  private static final Logger LOG = LoggerFactory.getLogger(CommentController.class);

  private final CommentService commentService;
  private final ArticleService articleService;
  private final ObjectMapper objectMapper;
  private final Service service;

  public CommentController(
      CommentService commentService,
      ArticleService articleService,
      ObjectMapper objectMapper,
      Service service) {
    this.service = service;
    this.commentService = commentService;
    this.articleService = articleService;
    this.objectMapper = objectMapper;
  }

  @Override
  public void initializeEndpoints() {
    createComment();
    deleteComment();
  }


  private void createComment() {
    service.post(
        "/api/comments",
        (Request request, Response response) -> {
          response.type("application/json");
          String body = request.body();
          CommentCreateRequest commentCreateRequest =
              objectMapper.readValue(body, CommentCreateRequest.class);
          try {
            articleService.findByID(commentCreateRequest.articleID());
          } catch (ArticleFindException e) {
            LOG.warn("Cannot find article", e);
            response.status(404);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }
          try {
            CommentId commentID =
                commentService.create(
                    commentCreateRequest.articleID(), commentCreateRequest.text());
            LOG.debug("Comment created");
            response.status(201);
            return objectMapper.writeValueAsString(new CommentCreateResponse(commentID));
          } catch (CommentCreateException e) {
            LOG.warn("Cannot create comment", e);
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }
        });
  }

  private void deleteComment() {
    service.delete(
        "/api/articles/:articleID/:commentID",
        (Request request, Response response) -> {
          response.type("application/json");
          ArticleId articleID = new ArticleId(Long.parseLong(request.params("articleID")));
          CommentId commentID = new CommentId(Long.parseLong(request.params("commentID")));
          CommentDeleteRequest commentDeleteRequest =
              new CommentDeleteRequest(articleID, commentID);
          try {
            articleService.findByID(commentDeleteRequest.articleID());
          } catch (ArticleFindException e) {
            LOG.warn("Cannot find article", e);
            response.status(404);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }
          try {
            commentService.delete(
                commentDeleteRequest.articleID(), commentDeleteRequest.commentID());
            LOG.debug("Comment deleted");
            response.status(200);
            return objectMapper.writeValueAsString("Comment deleted");
          } catch (CommentDeleteException e) {
            LOG.warn("Cannot delete comment", e);
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }
        });
  }
}