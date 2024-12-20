package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.controller.ArticleController.ArticleController;
import org.example.controller.ArticleController.ArticleFreemarkerController;
import org.example.controller.CommentController.CommentController;
import org.example.entity.Article;
import org.example.entity.Comment;
import org.example.id.ArticleId;
import org.example.repository.articleRepository.ArticleRepositoryImpl;
import org.example.repository.commentRepository.CommentRepositoryImpl;
import org.example.service.ArticleService;
import org.example.service.CommentService;
import org.example.template.TemplateFactory;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import spark.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class E2ETestcontainersTest {

  @Container
  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");

  private static Jdbi jdbi;
  private static Service service;

  @BeforeAll
  static void beforeAll() {
    String postgresJdbcUrl = POSTGRES.getJdbcUrl();
    Flyway flyway =
        Flyway.configure()
            .outOfOrder(true)
            .locations("classpath:db/migrations")
            .dataSource(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword())
            .load();
    flyway.migrate();
    jdbi = Jdbi.create(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword());
    service = Service.ignite();
  }

  @BeforeEach
  void beforeEach() {
    jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM article").execute());
    jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM comment").execute());
  }

  @AfterEach
  void afterEach() {
    service.stop();
    service.awaitStop();
  }

  @Test
  void E2ETest() throws IOException, InterruptedException {
    ObjectMapper objectMapper = new ObjectMapper();

    ArticleService articleService = new ArticleService(new ArticleRepositoryImpl(jdbi), new JdbiTransactionManager(jdbi));

    Application application = new Application(
        List.of(
            new ArticleController(service, articleService, objectMapper),
            new CommentController(new CommentService(new CommentRepositoryImpl(jdbi)), articleService, objectMapper, service),
            new ArticleFreemarkerController(service, articleService, TemplateFactory.freeMarkerEngine())
        )
    );
    application.start();
    service.awaitInitialization();

    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        """
                            { "name": "test", "tags": ["1", "2"] }
                            """
                    )
                )
                .uri(URI.create("http://localhost:4567/api/article"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(201, response.statusCode());

    HttpResponse<String> response1 = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        """
                            { "articleID":1, "text": "Test Text" }
                            """
                    )
                )
                .uri(URI.create("http://localhost:4567/api/comments"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(201, response1.statusCode());

    HttpResponse<String> response2 = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .PUT(
                    HttpRequest.BodyPublishers.ofString(
                        """
                            { "name": "testModified", "tags": ["modified"] }
                            """
                    )
                )
                .uri(URI.create("http://localhost:4567/api/articles/1"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(201, response2.statusCode());

    HttpResponse<String> response3 = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:4567/api/articles/1/1"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(200, response3.statusCode());

    HttpResponse<String> response4 = HttpClient.newHttpClient()
        .send(
            HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/api/articles/1"))
                .build(),
            HttpResponse.BodyHandlers.ofString(UTF_8)
        );

    assertEquals(200, response4.statusCode());

    Article article = articleService.findByID(new ArticleId(1L));

    String correctName = "testModified";

    Set<String> listCorrectTags = Set.of("[modified]");

    List<Comment> correctComments = new ArrayList<>(0);

    assertEquals(article.getName(), correctName);
    assertEquals(article.getSetTags(), listCorrectTags);
    assertEquals(article.getComments(), correctComments);
  }
}

