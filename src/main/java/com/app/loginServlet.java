package com.app;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/login")
public class loginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public loginServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        System.out.println(username);
        String password = request.getParameter("password");
        String role = request.getParameter("role"); // Retrieve the selected role
        System.out.println(role);
        // Database Connection
        String url = "jdbc:mysql://localhost:3306/rfb";
        String dbUsername = "root";
        String dbPassword = "Sreej@2005";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish Connection
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            // Prepare SQL query to retrieve user from the database based on username, password, and role
            String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
            statement = conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role); // Add role parameter to the query

            // Execute the query
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // User exists in the database with the provided role, redirect accordingly
                if ("admin".equals(role)) {
                    response.sendRedirect("admin.html");
                } else if ("mentor".equals(role)) {
                    response.sendRedirect("mentor.html");
                } else if ("mentee".equals(role)) {
                    response.sendRedirect("mentee.html");
                } else {
                    // Handle unrecognized role
                    response.sendRedirect("login.jsp?error=invalid_role");
                }
            } else {
                // User does not exist or incorrect credentials
                response.sendRedirect("login.jsp?error=invalid_credentials");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            // Handle database connection or query errors
            response.sendRedirect("login.jsp?error=database_error");
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
