CREATE TABLE comment
(
    comment_id BIGSERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    article_id BIGINT NOT NULL
)