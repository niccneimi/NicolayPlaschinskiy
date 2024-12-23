package org.example.repository.articleRepository;

import org.example.entity.Article;
import org.example.id.ArticleId;
import org.example.repository.articleRepository.exception.ArticleNotFoundException;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.jdbi.v3.core.Jdbi;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

@Testcontainers
class ArticleRepositoryTestcontainersTest {

    @Container
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");

    private static Jdbi jdbi;
    private static ArticleRepositoryImpl articleRepository;

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
    }

    @BeforeEach
    void beforeEach() {
        jdbi.useTransaction(handle -> {
            handle.createUpdate("DELETE FROM article").execute();
            handle.createUpdate("DELETE FROM comment").execute();
        });
        articleRepository = new ArticleRepositoryImpl(jdbi);
    }

    @Test
    void shouldGenerateNewArticleId() {
        ArticleId newId = articleRepository.generateID();
        assertNotNull(newId);
    }

    @Test
    void shouldCreateAndFindArticle() {
        Article article = new Article(articleRepository.generateID(), "Test Article", Set.of("tag1", "tag2"), List.of(), false);
        articleRepository.create(article);

        Article foundArticle = articleRepository.findByID(article.getID());
        assertEquals(article.getName(), foundArticle.getName());
        assertEquals(article.getSetTags(), foundArticle.getSetTags());
    }

    @Test
    void shouldGetAllArticles() {
        Article article1 = new Article(articleRepository.generateID(), "Article 1", Set.of("tag1"), List.of(), false);
        Article article2 = new Article(articleRepository.generateID(), "Article 2", Set.of("tag2"), List.of(), true);
        articleRepository.create(article1);
        articleRepository.create(article2);

        List<Article> articles = articleRepository.getArticles();
        assertEquals(2, articles.size());
    }

    @Test
    void shouldUpdateArticle() {
        Article article = new Article(articleRepository.generateID(), "Old Name", Set.of("tag1"), List.of(), false);
        articleRepository.create(article);
        Article preupdatedArticle = new Article(article.getID(), "Updated Name", article.getSetTags(), article.getComments(),article.getTrending());
        articleRepository.update(preupdatedArticle);

        Article updatedArticle = articleRepository.findByID(article.getID());
        assertEquals("Updated Name", updatedArticle.getName());
    }

    @Test
    void shouldDeleteArticle() {
        Article article = new Article(articleRepository.generateID(), "Article to Delete", Set.of("tag1"), List.of(), false);
        articleRepository.create(article);

        articleRepository.delete(article.getID());

        assertThrows(ArticleNotFoundException.class, () -> articleRepository.findByID(article.getID()));
    }

    @Test
    void shouldThrowExceptionWhenArticleNotFound() {
        ArticleId nonExistentId = new ArticleId(999L);
        assertThrows(ArticleNotFoundException.class, () -> articleRepository.findByID(nonExistentId));
    }
}