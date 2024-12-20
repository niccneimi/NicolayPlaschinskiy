package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.List;
import org.example.controller.ArticleController.ArticleController;
import org.example.controller.ArticleController.ArticleFreemarkerController;
import org.example.controller.CommentController.CommentController;
import org.example.repository.articleRepository.ArticleRepositoryImpl;
import org.example.repository.commentRepository.CommentRepositoryImpl;
import org.example.service.ArticleService;
import org.example.service.CommentService;
import org.example.template.TemplateFactory;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;

import spark.Service;

public class Main {
    public static void main(String[] args) {
        Service service = Service.ignite();
        Config config = ConfigFactory.load();

        Flyway flyway =
                Flyway.configure()
                .outOfOrder(true)
                .locations("classpath:db/migrations")
                .dataSource(config.getString("app.database.url"), config.getString("app.database.user"),
                        config.getString("app.database.password"))
                .load();
        flyway.migrate();

        Jdbi jdbi = Jdbi.create(config.getString("app.database.url"), config.getString("app.database.user"),
                config.getString("app.database.password"));
        
        ObjectMapper objectMapper = new ObjectMapper();
        ArticleRepositoryImpl articleRepositoryImpl = new ArticleRepositoryImpl(jdbi);
        CommentRepositoryImpl commentRepositoryImpl = new CommentRepositoryImpl(jdbi);
        final ArticleService articleService = new ArticleService(articleRepositoryImpl, new JdbiTransactionManager(jdbi));
        Application application = new Application(
                List.of(
                        new ArticleController(service, articleService, objectMapper),
                        new CommentController(
                                new CommentService(commentRepositoryImpl),
                                articleService,
                                objectMapper,
                                service),
                        new ArticleFreemarkerController(
                                service, articleService, TemplateFactory.freeMarkerEngine())));
        application.start();
    }
}