SELECT p.post_id
FROM post p
JOIN (
    SELECT post_id, COUNT(*) AS comment_count
    FROM comment
    GROUP BY post_id
) c ON p.post_id = c.post_id
WHERE c.comment_count = 2
  AND p.title ~ '^[0-9]'
  AND LENGTH(p.content) > 20
ORDER BY p.post_id ASC;