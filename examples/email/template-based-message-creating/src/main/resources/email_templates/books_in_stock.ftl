<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <#include "/email_templates/books_in_stock.css">
</head>
<body>

<div class="container">
  <table class="responsive-table">
    <caption>Books available in stock</caption>
    <thead>
      <tr>
        <th scope="col">Name</th>
        <th scope="col">Author</th>
      </tr>
    </thead>
    <tbody>
      <#list books as book>
      <tr>
        <td data-title="Name" align="left">${book.name}</td>
        <td data-title="Author">${book.author}</td>
      </tr>
      </#list>
    </tbody>
  </table>
</div>

</body>
</html>