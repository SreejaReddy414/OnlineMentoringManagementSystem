package com.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SubmitAnswerServlet")
public class SubmitAnswerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // Extract parameters from the form submission
        int doubtId = Integer.parseInt(request.getParameter("doubtId"));
        String studentRollNo = request.getParameter("studentRollNo");
        String doubtText = request.getParameter("doubtText");
        String audioPath = request.getParameter("audioPath");
        String answer = request.getParameter("answer");

        try {
            // Establish connection to the database
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/rfb", "root", "Sreej@2005");

            // Insert the answer into the answers table
            String insertAnswerSQL = "INSERT INTO answers (doubt_id, student_roll_no, doubt_text, audio_path, answer) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertAnswerStmt = con.prepareStatement(insertAnswerSQL);
            insertAnswerStmt.setInt(1, doubtId);
            insertAnswerStmt.setString(2, studentRollNo);
            insertAnswerStmt.setString(3, doubtText);
            insertAnswerStmt.setString(4, audioPath);
            insertAnswerStmt.setString(5, answer);
            int rowsAffected = insertAnswerStmt.executeUpdate();

            if (rowsAffected > 0) {
                // If the answer is successfully inserted, update the doubt status
                String updateDoubtStatusSQL = "UPDATE doubts SET status = 'Answered' WHERE doubt_id = ?";
                PreparedStatement updateDoubtStatusStmt = con.prepareStatement(updateDoubtStatusSQL);
                updateDoubtStatusStmt.setInt(1, doubtId);
                int updateRowsAffected = updateDoubtStatusStmt.executeUpdate();

                if (updateRowsAffected > 0) {
                    // If the doubt status is successfully updated, display success message
                    out.println("<p>Answer submitted successfully. Doubt status updated.</p>");
                } else {
                    out.println("<p>Failed to update doubt status.</p>");
                }
            } else {
                out.println("<p>Failed to submit answer.</p>");
            }

            con.close();
        } catch (Exception e) {
            out.println("<p>Error: " + e.getMessage() + "</p>");
        }

        out.close();
    }
}
