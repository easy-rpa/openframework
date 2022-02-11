<html>
    <head>
        <#include "/email_templates/books_proposition.css">
    </head>
    <body>
        <div class="message-body">
            <p>
              Dear customer,
            <p/>
            <p>
              Today our bookshop can propose you to buy following books
            </p>
            <table>
                <tr><th class="first">Name</th><th>Author</th></tr>
                <#list books as book>
                    <tr class="row${book_index % 2}"><td>${book.getName()}</td><td>${book.getAuthor()}</td></tr>
                </#list>
            </table>
        </div>
    </body>
</html>