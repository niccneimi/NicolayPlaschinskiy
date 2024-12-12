package org.example;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.example.controller.ArticleController.ArticleController;
import org.example.controller.CommentController.CommentController;
import org.example.repository.articleRepository.InMemoryArticleRepository;
import org.example.repository.commentRepository.InMemoryCommentRepository;
import org.example.service.ArticleService;
import org.example.service.CommentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Service;

class ApplicationTest {

  private Service service;

  @BeforeEach
  void befofeEach() {
    service = Service.ignite();
  }

  @AfterEach
  void afterEach() {
    service.stop();
    service.awaitStop();
  }

  @Test
  void E2ETest() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    InMemoryArticleRepository inMemoryArticleRepository = new InMemoryArticleRepository();
    final ArticleService articleService = new ArticleService(inMemoryArticleRepository);
    Application application =
        new Application(
            List.of(
                new ArticleController(service, articleService, objectMapper),
                new CommentController(
                    new CommentService(new InMemoryCommentRepository(inMemoryArticleRepository)),
                    articleService,
                    objectMapper,
                    service)));
    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .POST(
                        HttpRequest.BodyPublishers.ofString(
                            """
                                                    { "name": "How to use Postman", "tags": ["API", "Request", "Response"] }"""))
                    .uri(URI.create("http://localhost:%d/api/articles".formatted(service.port())))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(201, response.statusCode());

    HttpResponse<String> response1 =
    HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        """
                                { "articleID": 1, "text": "Impressive!" }"""))
                .uri(URI.create("http://localhost:%d/api/comments".formatted(service.port())))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(201, response1.statusCode());

    HttpResponse<String> response2 =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .PUT(
                        HttpRequest.BodyPublishers.ofString(
                            """
                                                    { "name": "How to use Spring", "tags": ["API", "Request"] }"""))
                    .uri(URI.create("http://localhost:%d/api/articles/1".formatted(service.port())))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(201, response2.statusCode());

    HttpResponse<String> response3 =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .DELETE()
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/articles/1/1".formatted(service.port())))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(200, response3.statusCode());

    HttpResponse<String> response4 =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://localhost:%d/api/articles/1".formatted(service.port())))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(200, response4.statusCode());
    System.out.println(response4.body());
  }
}