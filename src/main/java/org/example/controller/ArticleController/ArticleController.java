package org.example.controller.ArticleController;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.id.ArticleId;
import org.example.controller.Controller;
import org.example.controller.ErrorResponse;
import org.example.controller.ArticleController.request.ArticleCreateRequest;
import org.example.controller.ArticleController.request.ArticleDeleteRequest;
import org.example.controller.ArticleController.request.ArticleFindRequest;
import org.example.controller.ArticleController.request.ArticleUpdateRequest;
import org.example.controller.ArticleController.response.ArticleCreateResponse;
import org.example.controller.ArticleController.response.ArticleDeleteResponse;
import org.example.controller.ArticleController.response.ArticleFindResponse;
import org.example.controller.ArticleController.response.ArticleUpdateResponse;
import org.example.entity.Article;
import org.example.service.ArticleService;
import org.example.service.exception.ArticleCreateException;
import org.example.service.exception.ArticleDeleteException;
import org.example.service.exception.ArticleFindException;
import org.example.service.exception.ArticleUpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;


public class ArticleController implements Controller {

  private static final Logger LOG = LoggerFactory.getLogger(ArticleController.class);

  private final ArticleService articleService;
  private final ObjectMapper objectMapper;
  private final Service service;

  public ArticleController(
      Service service, ArticleService articleService, ObjectMapper objectMapper) {
    this.service = service;
    this.articleService = articleService;
    this.objectMapper = objectMapper;
  }

  @Override
  public void initializeEndpoints() {
    createArticle();
    updateArticle();
    findArticle();
    getArticles();
    deleteArticle();
  }

  private void createArticle() {
    service.post(
        "/api/articles",
        (Request request, Response response) -> {
          response.type("application/json");
          String body = request.body();
          ArticleCreateRequest articleCreateRequest =
              objectMapper.readValue(body, ArticleCreateRequest.class);
          try {
            ArticleId articleID =
                articleService.create(articleCreateRequest.name(), articleCreateRequest.tags());
            LOG.debug("Article created");
            response.status(201);
            return objectMapper.writeValueAsString(new ArticleCreateResponse(articleID));
          } catch (ArticleCreateException e) {
            LOG.warn("Cannot create article", e);
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }
        });
  }


  private void updateArticle() {
    service.put(
        "/api/articles/:articleID",
        (Request request, Response response) -> {
          response.type("application/json");
          ArticleId articleID = new ArticleId(Long.parseLong(request.params("articleID")));
          ArticleUpdateRequest articleUpdateRequest =
              objectMapper.readValue(request.body(), ArticleUpdateRequest.class);
          try {
            articleService.update(
                articleID, articleUpdateRequest.name(), articleUpdateRequest.tags());
            LOG.debug("Article updated");
            response.status(201);
            return objectMapper.writeValueAsString(new ArticleUpdateResponse());
          } catch (ArticleUpdateException e) {
            LOG.warn("Cannot update article", e);
            response.status(400);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }
        });
  }


  private void findArticle() {
    service.get(
        "/api/articles/:articleID",
        (Request request, Response response) -> {
          response.type("application/json");
          ArticleId articleID = new ArticleId(Long.parseLong(request.params("articleID")));
          ArticleFindRequest articleFindRequest = new ArticleFindRequest(articleID);
          try {
            Article article = articleService.findByID(articleFindRequest.articleID());
            LOG.debug("Article found");
            response.status(200);
            return objectMapper.writeValueAsString(new ArticleFindResponse(article));
          } catch (ArticleFindException e) {
            LOG.warn("Cannot find article", e);
            response.status(404);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }
        });
  }


  private void getArticles() {
    service.get(
        "/api/articles",
        (Request request, Response response) -> {
          response.type("application/json");
          List<Article> articles = articleService.getArticles();
          List<Map<String, String>> articleMapList =
              articles.stream()
                  .map(
                      article ->
                          Map.of(
                              "name",
                              article.getName(),
                              "tags",
                              article.getTags(),
                              "comments",
                              article.getCommentsAPI()))
                  .toList();

          Map<String, Object> model = new HashMap<>();
          model.put("articles", articleMapList);
          LOG.debug("Articles showed");
          return objectMapper.writeValueAsString(model);
        });
  }

  private void deleteArticle() {
    service.delete(
        "/api/articles/:articleID",
        (Request request, Response response) -> {
          response.type("application/json");
          ArticleId articleID = new ArticleId(Long.parseLong(request.params("articleID")));
          ArticleDeleteRequest articleDeleteRequest = new ArticleDeleteRequest(articleID);
          try {
            articleService.delete(articleDeleteRequest.articleID());
            LOG.debug("Article deleted");
            response.status(200);
            return objectMapper.writeValueAsString(new ArticleDeleteResponse());
          } catch (ArticleDeleteException e) {
            LOG.warn("Cannot delete article", e);
            response.status(404);
            return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
          }
        });
  }
}