package org.example.controller.articleController;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.example.Application;
import org.example.JdbiTransactionManager;
import org.example.repository.articleRepository.ArticleRepositoryImpl;
import org.example.service.ArticleService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.example.controller.ArticleController.ArticleController;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.jdbi.v3.core.Jdbi;
import spark.Service;

@Testcontainers
class ArticleControllerTest {

    @Container
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");

    private Service service;
    private Jdbi jdbi;

    @BeforeAll
    static void beforeAll() {
        String postgresJdbcUrl = POSTGRES.getJdbcUrl();
        Flyway flyway = Flyway.configure()
                .outOfOrder(true)
                .locations("classpath:db/migrations")
                .dataSource(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword())
                .load();
        flyway.migrate();
    }

    @BeforeEach
    void beforeEach() {
        String postgresJdbcUrl = POSTGRES.getJdbcUrl();
        service = Service.ignite();
        jdbi = Jdbi.create(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword());
        jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM article").execute());
        jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM comment").execute());
    }

    @AfterEach
    void afterEach() {
        service.stop();
        service.awaitStop();
    }

    @Test
    void should200IfArticleIsSuccessfullyCreated() throws Exception {
        final ArticleService articleService = new ArticleService(new ArticleRepositoryImpl(jdbi),
                new JdbiTransactionManager(jdbi));
        ObjectMapper objectMapper = new ObjectMapper();
        Application application = new Application(
                List.of(new ArticleController(service, articleService, objectMapper)));
        application.start();
        service.awaitInitialization();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .POST(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        [{ "name": "How to use Postman", "tags": ["API", "Request", "Response"] }]"""))
                                .uri(URI.create("http://localhost:%d/api/articles".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, response.statusCode());
    }

    @Test
    void should200IfArticleSuccessfullyDeleted() throws Exception {
        final ArticleService articleService = new ArticleService(new ArticleRepositoryImpl(jdbi),
                new JdbiTransactionManager(jdbi));
        ObjectMapper objectMapper = new ObjectMapper();
        Application application = new Application(
                List.of(new ArticleController(service, articleService, objectMapper)));
        application.start();
        service.awaitInitialization();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .POST(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        [
                                                          { "name": "How to use Postman", "tags": ["API", "Request", "Response"] }
                                                        ]
                                                        """))
                                .uri(URI.create("http://localhost:%d/api/articles".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());

        HttpResponse<String> response1 = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .DELETE()
                                .uri(URI.create("http://localhost:%d/api/articles/1".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(200, response1.statusCode());
    }

    @Test
    void should404IfArticleDoesNotDeleted() throws Exception {
        final ArticleService articleService = new ArticleService(new ArticleRepositoryImpl(jdbi),
                new JdbiTransactionManager(jdbi));
        ObjectMapper objectMapper = new ObjectMapper();
        Application application = new Application(
                List.of(new ArticleController(service, articleService, objectMapper)));
        application.start();
        service.awaitInitialization();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .DELETE()
                                .uri(URI.create("http://localhost:%d/api/articles/1".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(404, response.statusCode());
    }

    @Test
    void should200IfArticleSuccessfullyUpdated() throws Exception {
        final ArticleService articleService = new ArticleService(new ArticleRepositoryImpl(jdbi),
                new JdbiTransactionManager(jdbi));
        ObjectMapper objectMapper = new ObjectMapper();
        Application application = new Application(
                List.of(new ArticleController(service, articleService, objectMapper)));
        application.start();
        service.awaitInitialization();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .POST(
                                        HttpRequest.BodyPublishers.ofString(
                                            """
                                                [
                                                  {"name": "How to use Postman", "tags": ["API", "Request", "Response"] }
                                                ]
                                                """))
                                .uri(URI.create("http://localhost:%d/api/articles".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        HttpResponse<String> response1 = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                            """
                                                  {"name": "How to use Spring", "tags": ["API", "Request"] }
                                                """))
                                .uri(URI.create("http://localhost:%d/api/articles/3".formatted(service.port(), response.body())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(201, response1.statusCode());
    }
}