SELECT p.post_id
FROM post p
LEFT JOIN (
    SELECT post_id, COUNT(*) AS comment_count
    FROM comment
    GROUP BY post_id
) c ON p.post_id = c.post_id
WHERE c.comment_count IS NULL OR c.comment_count = 1
ORDER BY p.post_id ASC
LIMIT 10;