<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Articles List</title>
</head>
<body>
    <h1>Articles</h1>
    <ul>
        <#list articles as article>
            <li>${article.title} - Comments: ${article.comments?size}</li>
        </#list>
    </ul>
</body>
</html>
