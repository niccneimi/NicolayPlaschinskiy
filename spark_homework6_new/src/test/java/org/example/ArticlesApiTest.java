package org.example;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.example.api.ArticlesApi;
import org.example.model.*;
import org.example.repository.InMemoryRepository;

public class ArticlesApiTest {

   private InMemoryRepository repository;
   private ArticlesApi api;

   @BeforeEach
   void setup() {
       repository = new InMemoryRepository();
       api = new ArticlesApi(repository);
   }

   @Test
   void testEndToEndScenario() {
       // Создаем статью
       Article article = new Article(repository.generateArticleId(), "Test Title", Set.of("tag1"), List.of());
       repository.addArticle(article);

       // Добавляем в нее комментарий
       Comment comment = new Comment(repository.generateCommentId(), article.getId(), "Test Comment");
       repository.addCommentToArticle(article.getId(), comment);

       // Редактируем статью
       article = article.withTitle("Updated Title");
       repository.updateArticle(article);

       // Удаляем комментарий
       repository.deleteCommentFromArticle(article.getId(), comment.getId());

       // Запрашиваем статью по ID и проверяем состояние
       Optional<Article> updatedArticleOpt = repository.findArticleById(article.getId());
       assertTrue(updatedArticleOpt.isPresent());
       assertEquals("Updated Title", updatedArticleOpt.get().getTitle());
       assertTrue(updatedArticleOpt.get().getComments().isEmpty());
   }
}
