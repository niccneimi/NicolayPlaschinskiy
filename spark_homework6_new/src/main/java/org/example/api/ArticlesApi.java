package org.example.api;

import static spark.Spark.*;

import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;

import org.example.model.*;

import org.example.repository.InMemoryRepository;

public class ArticlesApi {

    private final InMemoryRepository repository;
    private final FreeMarkerEngine freeMarkerEngine;
    private final Gson gson = new Gson();

    public ArticlesApi(InMemoryRepository repository) {
        this.repository = repository;

        // Настройка FreeMarker
        Configuration freeMarkerConfig = new Configuration(Configuration.VERSION_2_3_23);
        freeMarkerConfig.setClassForTemplateLoading(ArticlesApi.class, "/resources/");
        freeMarkerConfig.setDefaultEncoding("UTF-8");
        freeMarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        this.freeMarkerEngine = new FreeMarkerEngine(freeMarkerConfig);

        get("/articles/template", (req, res) -> {
            List<Article> allArticles = repository.findAllArticles();
            Map<String, Object> model = new HashMap<>();
            model.put("articles", allArticles);
            return freeMarkerEngine.render(new ModelAndView(model, "articles.ftl"));
        });

        path("/articles", () -> {
            get("", (req, res) -> gson.toJson(repository.findAllArticles()), gson::toJson);
            post("", (req, res) -> {
                System.out.println("BBBBBBBBBB");
                Article article = gson.fromJson(req.body(), Article.class);
                repository.addArticle(article);
                res.status(201); // Created
                return gson.toJson(article);
            });

            get("/:id", (req, res) -> {
                ArticleId id = new ArticleId(Long.parseLong(req.params(":id")));
                Optional<Article> articleOpt = repository.findArticleById(id);
                if (articleOpt.isPresent()) {
                    return gson.toJson(articleOpt.get());
                } else {
                    res.status(404); // Not Found
                    return "Article not found";
                }
            });
            put("/:id", (req, res) -> {
                ArticleId id = new ArticleId(Long.parseLong(req.params(":id")));
                Optional<Article> existingArticleOpt = repository.findArticleById(id);
                if (!existingArticleOpt.isPresent()) {
                    res.status(404);
                    return "Article not found";
                }
                Article existingArticle = existingArticleOpt.get();
                Article updatedArticle = gson.fromJson(req.body(), Article.class);
                Article articleToUpdate = new Article(existingArticle.getId(), updatedArticle.getTitle(),
                        existingArticle.getTags(), existingArticle.getComments());

                repository.updateArticle(articleToUpdate);

                return gson.toJson(articleToUpdate);
            });

            delete("/:id", (req, res) -> {
                repository.deleteArticle(new ArticleId(Long.parseLong(req.params(":id"))));
                res.status(204); // No Content
                return "";
            });
        });

        path("/comments", () -> {
            post("", (req, res) -> {
                Comment comment = gson.fromJson(req.body(), Comment.class);
                repository.addCommentToArticle(comment.getArticleId(), comment);
                res.status(201); // Created
                return gson.toJson(comment);
            });

            delete("/:articleId/:commentId", (req, res) -> {
                repository.deleteCommentFromArticle(
                        new ArticleId(Long.parseLong(req.params(":articleId"))),
                        new CommentId(Long.parseLong(req.params(":commentId"))));
                res.status(204); // No Content
                return "";
            });
        });

        exception(Exception.class, (e, req, res) -> {
            res.status(500);
            System.err.println("Error: " + e.getMessage());
        });

        after((req, res) -> {
            System.out.println("Request handled successfully");
        });

        afterAfter((req, res) -> {
            if (res.status() >= 400 && res.status() < 500) {
                System.err.println("Client error occurred: " + res.status());
            } else if (res.status() >= 500) {
                System.err.println("Server error occurred: " + res.status());
            }
        });
    }

    public static void main(String[] args) {
        InMemoryRepository repository = new InMemoryRepository();
        new ArticlesApi(repository);
    }
}
