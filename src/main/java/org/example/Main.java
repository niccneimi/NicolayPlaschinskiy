package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.example.controller.ArticleController.ArticleController;
import org.example.controller.ArticleController.ArticleFreemarkerController;
import org.example.controller.CommentController.CommentController;
import org.example.repository.articleRepository.InMemoryArticleRepository;
import org.example.repository.commentRepository.InMemoryCommentRepository;
import org.example.service.ArticleService;
import org.example.service.CommentService;
import org.example.template.TemplateFactory;
import spark.Service;

public class Main {
    public static void main(String[] args) {
        Service service = Service.ignite();
        ObjectMapper objectMapper = new ObjectMapper();
        InMemoryArticleRepository inMemoryArticleRepository = new InMemoryArticleRepository();
        final ArticleService articleService = new ArticleService(inMemoryArticleRepository);
        Application application = new Application(
                List.of(
                        new ArticleController(service, articleService, objectMapper),
                        new CommentController(
                                new CommentService(new InMemoryCommentRepository(inMemoryArticleRepository)),
                                articleService,
                                objectMapper,
                                service),
                        new ArticleFreemarkerController(
                                service, articleService, TemplateFactory.freeMarkerEngine())));
        application.start();
    }
}