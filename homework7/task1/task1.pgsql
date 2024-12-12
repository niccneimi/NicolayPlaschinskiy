SELECT COUNT(*) AS no_posts_count
FROM profile p
LEFT JOIN post po ON p.profile_id = po.profile_id
WHERE po.post_id IS NULL;