package org.example.repository.articleRepository;

import org.example.entity.Article;
import org.example.entity.Comment;
import org.example.id.ArticleId;
import org.example.id.CommentId;
import org.example.repository.articleRepository.exception.ArticleNotFoundException;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Set;

public class ArticleRepositoryImpl implements ArticleRepository {

    private final Jdbi jdbi;

    public ArticleRepositoryImpl(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public ArticleId generateID() {
        return new ArticleId(jdbi.withHandle(handle -> handle.createQuery("SELECT nextval('article_article_id_seq') AS value")
                .mapTo(Long.class)
                .one()));
    }

    @Override
    public List<Article> getArticles() {
        return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM article")
                .map((rs, ctx) -> {
                    ArticleId articleId = new ArticleId(rs.getLong("article_id"));
                    String name = rs.getString("name");
                    Set<String> tags = Set.of((String[]) rs.getArray("tags").getArray());
                    boolean trending = rs.getBoolean("trending");
                    List<Comment> comments = handle.createQuery("SELECT * FROM comment WHERE article_id = :articleId")
                            .bind("articleId", articleId.getValue())
                            .map((commentRs, commentCtx) -> new Comment(
                                    new CommentId(commentRs.getLong("comment_id")),
                                    commentRs.getString("text"),
                                    articleId))
                            .list();

                    return new Article(articleId, name, tags, comments, trending);
                })
                .list());
    }

    @Override
    public Article findByID(ArticleId ID) {
        return jdbi.withHandle(handle -> {
            Article article = handle.createQuery("SELECT * FROM article WHERE article_id = :id")
                    .bind("id", ID.getValue())
                    .map((rs, ctx) -> {
                        ArticleId articleId = new ArticleId(rs.getLong("article_id"));
                        String name = rs.getString("name");
                        Set<String> tags = Set.of((String[]) rs.getArray("tags").getArray());
                        boolean trending = rs.getBoolean("trending");
                        List<Comment> comments = handle
                                .createQuery("SELECT * FROM comment WHERE article_id = :articleId")
                                .bind("articleId", articleId.getValue())
                                .map((commentRs, commentCtx) -> new Comment(
                                        new CommentId(commentRs.getLong("comment_id")),
                                        commentRs.getString("text"),
                                        articleId))
                                .list();

                        return new Article(articleId, name, tags, comments, trending);
                    })
                    .findOne()
                    .orElseThrow(() -> new ArticleNotFoundException("Cannot find article with ID=" + ID));
            return article;
        });
    }

    @Override
    public void create(Article article) {
        jdbi.useHandle(handle -> {
            handle.createUpdate(
                    "INSERT INTO article (article_id, name, tags, trending) VALUES (:id, :name, :tags, :trending)")
                    .bind("id", article.getID().getValue())
                    .bind("name", article.getName())
                    .bindArray("tags", String.class, article.getSetTags())
                    .bind("trending", article.getTrending())
                    .execute();
        });
    }

    @Override
    public void update(Article article) {
        jdbi.useHandle(handle -> {
            if (handle.createQuery("SELECT COUNT(*) FROM article WHERE article_id = :id")
                    .bind("id", article.getID().getValue())
                    .mapTo(Long.class)
                    .one() == 0) {
                throw new ArticleNotFoundException("Cannot find article with ID=" + article.getID());
            }
            handle.createUpdate(
                    "UPDATE article SET name = :name, tags = :tags, trending = :trending WHERE article_id = :id")
                    .bind("id", article.getID().getValue())
                    .bind("name", article.getName())
                    .bindArray("tags", String.class, article.getTags())
                    .bind("trending", article.getTrending())
                    .execute();
        });
    }

    @Override
    public void delete(ArticleId ID) {
        jdbi.useHandle(handle -> {
            int deletedRows = handle.createUpdate("DELETE FROM article WHERE article_id = :id")
                    .bind("id", ID.getValue())
                    .execute();
            if (deletedRows == 0) {
                throw new ArticleNotFoundException("Cannot find article with ID=" + ID);
            }
        });
    }
}