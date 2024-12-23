CREATE TABLE article
(
    article_id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    tags TEXT[] NOT NULL,
    comments_id BIGINT[],
    trending BOOLEAN NOT NULL
)