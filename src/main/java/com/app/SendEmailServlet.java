package com.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/sendEmail")
public class SendEmailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/rfb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Sreej@2005";
    private static final Logger LOGGER = Logger.getLogger(SendEmailServlet.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String parentEmail = request.getParameter("parentEmail");
        String reportType = request.getParameter("reportType");
        String rollNo = request.getParameter("rollNo");
        String data = request.getParameter("data");
        String template = request.getParameter("template");
        String employeeId = request.getParameter("employeeId");

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            saveReport(parentEmail, reportType, rollNo, data, template, employeeId);
            out.write("Email details saved successfully!");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing request", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("Failed to process request. Please try again later.");
        } finally {
            out.close();
        }
    }

    private void saveReport(String parentEmail, String reportType, String rollNo, String data, String template, String employeeId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO reports (parent_email, report_type, roll_no, data, template, employee_id) VALUES (?, ?, ?, ?, ?, ?)")) {

            Class.forName("com.mysql.cj.jdbc.Driver");
            pstmt.setString(1, parentEmail);
            pstmt.setString(2, reportType);
            pstmt.setString(3, rollNo);
            pstmt.setString(4, data);
            pstmt.setString(5, template);
            pstmt.setString(6, employeeId);

            pstmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Database error", e);
        }
    }
}
