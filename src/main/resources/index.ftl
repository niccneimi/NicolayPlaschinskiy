<html lang="ru">
<head>
  <meta charset="UTF-8">
  <title>Главная страница</title>
</head>

<body>

<h1>Список статей</h1>
<table>
  <tr>
    <th>Название</th>
    <th>Теги</th>
    <th>Количество комментариев</th>
  </tr>
    <#list articles as article>
      <tr>
        <td>${article.name}</td>
        <td>${article.tags}</td>
        <td>${article.comments}</td>
      </tr>
    </#list>
</table>

</body>

</html>