package org.example.repository.commentRepository;

import java.util.List;
import java.util.Set;

import org.example.entity.Article;
import org.example.entity.Comment;
import org.example.id.ArticleId;
import org.example.id.CommentId;
import org.example.repository.commentRepository.exception.CommentIDDuplicatedException;
import org.example.repository.commentRepository.exception.CommentNotFoundException;
import org.jdbi.v3.core.Jdbi;

public class CommentRepositoryImpl implements CommentRepository {

    private final Jdbi jdbi;

    public CommentRepositoryImpl(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public CommentId generateID() {
        return new CommentId(jdbi.withHandle(handle -> handle.createQuery("SELECT nextval('comment_comment_id_seq')")
                .mapTo(Long.class)
                .one()));
    }

    @Override
    public Comment findByID(ArticleId articleID, CommentId commentID) {
        return jdbi.withHandle(handle -> {
            Comment comment = handle
                    .createQuery("SELECT * FROM comment WHERE article_id = :articleId AND comment_id = :commentId")
                    .bind("articleId", articleID.getValue())
                    .bind("commentId", commentID.getValue())
                    .map((rs, ctx) -> new Comment(
                            new CommentId(rs.getLong("comment_id")),
                            rs.getString("text"),
                            articleID))
                    .findOne()
                    .orElseThrow(() -> new CommentNotFoundException("Cannot find comment with ID=" + commentID));
            return comment;
        });
    }

    @Override
    public void create(ArticleId articleID, Comment comment) {
        jdbi.useHandle(handle -> {
            handle.begin();

            handle.createQuery("SELECT * FROM article WHERE article_id = :articleId FOR UPDATE")
                    .bind("articleId", articleID.getValue())
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
                    .one();

            if (handle
                    .createQuery(
                            "SELECT COUNT(*) FROM comment WHERE article_id = :articleId AND comment_id = :commentId")
                    .bind("articleId", articleID.getValue())
                    .bind("commentId", comment.getID().getValue())
                    .mapTo(Long.class)
                    .one() > 0) {
                throw new CommentIDDuplicatedException("Comment with the given id already exists: " + comment.getID());
            }

            handle.createUpdate(
                    "INSERT INTO comment (comment_id, text, article_id) VALUES (:commentId, :text, :articleId)")
                    .bind("commentId", comment.getID().getValue())
                    .bind("text", comment.getText())
                    .bind("articleId", articleID.getValue())
                    .execute();

            int commentCount = handle.createQuery("SELECT COUNT(*) FROM comment WHERE article_id = :articleId")
                    .bind("articleId", articleID.getValue())
                    .mapTo(Integer.class)
                    .one();

            boolean trending = commentCount > 3;
            handle.createUpdate("UPDATE article SET trending = :trending WHERE article_id = :articleId")
                    .bind("trending", trending)
                    .bind("articleId", articleID.getValue())
                    .execute();

            handle.commit();
        });
    }

    @Override
    public void delete(ArticleId articleID, CommentId commentID) {
        jdbi.useHandle(handle -> {
            handle.begin();

            handle.createQuery("SELECT * FROM article WHERE article_id = :articleId FOR UPDATE")
                    .bind("articleId", articleID.getValue())
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
                    .one();

            int deletedCount = handle
                    .createUpdate("DELETE FROM comment WHERE article_id = :articleId AND comment_id = :commentId")
                    .bind("articleId", articleID.getValue())
                    .bind("commentId", commentID.getValue())
                    .execute();
            if (deletedCount == 0) {
                throw new CommentNotFoundException("Cannot find comment with ID=" + commentID);
            }

            int commentCount = handle.createQuery("SELECT COUNT(*) FROM comment WHERE article_id = :articleId")
                    .bind("articleId", articleID.getValue())
                    .mapTo(Integer.class)
                    .one();

            boolean trending = commentCount > 3;
            handle.createUpdate("UPDATE article SET trending = :trending WHERE article_id = :articleId")
                    .bind("trending", trending)
                    .bind("articleId", articleID.getValue())
                    .execute();
                    
            handle.commit();
        });
    }
}