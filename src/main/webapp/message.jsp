<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Message</title>
</head>
<body>
    <h1>Message</h1>
    <p><%= request.getAttribute("Message") %></p>
    <a href="admin.html">Back to Admin Page</a>
</body>
</html>
