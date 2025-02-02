<!DOCTYPE html>
<html>
<head>
    <title>Login Page</title>
</head>
<body>
    <h2>Login</h2>
    <% 
        // Check if there's an error message from the servlet
        String error = request.getParameter("error");
        if (error != null) {
            if (error.equals("invalid_credentials")) {
    %>
                <p style="color: red;">Invalid username or password. Please try again.</p>
    <% 
            } else if (error.equals("database_error")) {
    %>
                <p style="color: red;">Database error. Please try again later.</p>
    <% 
            }
        }
    %>
    <form action="login" method="post">
        <label for="username">Username:</label><br>
        <input type="text" id="username" name="username"><br>
        <label for="password">Password:</label><br>
        <input type="password" id="password" name="password"><br><br>
        <input type="submit" value="Login">
    </form>
</body>
</html>
