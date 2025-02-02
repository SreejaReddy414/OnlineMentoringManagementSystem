package com.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@WebServlet("/ViewSr")
public class ViewSr extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Retrieve employee ID from the request
        String employeeID = request.getParameter("RollNumber");

        // Database connection parameters
        String url = "jdbc:mysql://localhost:3306/rfb";
        String username = "root";
        String password = "Sreej@2005";

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Connect to the database
            Connection conn = DriverManager.getConnection(url, username, password);

            // Create SQL statement to fetch reports for the given employee ID
            String sql = "SELECT parent_email, template, created_at, roll_no FROM reports WHERE roll_no=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, employeeID);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Display the reports
            out.println("<html><head><title>Reports</title><link rel=\"stylesheet\" href=\"vr.css\"></head><body>");
            out.println("<h2 >Reports for Student Roll No: " + employeeID + "</h2>");
            while (resultSet.next()) {
                out.println("<div class=\"report\">");
                out.println("<p>Parent Email: " + resultSet.getString("parent_email") + "</p>");
                out.println("<p>Template: " + resultSet.getString("template") + "</p>");
                out.println("<p>Date: " + resultSet.getString("created_at") + "</p>");
                out.println("<p>Student Roll No: " + resultSet.getString("roll_no") + "</p>");
                out.println("</div>");
            }

            // Close all connections
            resultSet.close();
            statement.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
