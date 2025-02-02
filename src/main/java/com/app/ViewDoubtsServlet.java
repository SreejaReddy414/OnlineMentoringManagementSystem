package com.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ViewDoubtsServlet")
public class ViewDoubtsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleRequest(request, response);
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String department = request.getParameter("department");

        try {
            // Establish connection to the database
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/rfb", "root", "Sreej@2005");

            // Query to get pending doubts sorted by year and roll number
            String sql = "SELECT d.doubt_id, d.student_roll_no, d.doubt_text, d.audio_path, d.submission_time, s.YearOfJoining " +
                         "FROM doubts d JOIN students s ON d.student_roll_no = s.RollNumber " +
                         "WHERE d.department = ? AND d.status = 'Pending' ORDER BY s.YearOfJoining, s.RollNumber";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, department);
            ResultSet rs = stmt.executeQuery();

            // Generate HTML output
            out.println("<html><head><title>Doubts Details</title>");
            out.println("<link rel='stylesheet' type='text/css' href='viewDoubts.css'></head><body>");
            out.println("<div class='container'>");
            out.println("<h2>Doubts in " + department + " Department:</h2>");
            out.println("<table>");
            out.println("<tr><th>Doubt ID</th><th>Student Roll No</th><th>Year of Joining</th><th>Doubt Text</th><th> Audio File</th><th>Submission Time</th><th>Answer</th></tr>");
            
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("doubt_id") + "</td>");
                out.println("<td>" + rs.getString("student_roll_no") + "</td>");
                out.println("<td>" + rs.getString("YearOfJoining") + "</td>");
                
                // Display doubt_text
                out.println("<td>" + rs.getString("doubt_text") + "</td>");
                
                // Display audio_path only if it is not null
                String audioPath = rs.getString("audio_path");
                if (audioPath != null) {
                    out.println("<td><a href='" + audioPath + "' target='_blank'>Audio File</a></td>");
                } else {
                    out.println("<td></td>"); // Display empty cell if audio_path is null
                }
                
                out.println("<td>" + rs.getTimestamp("submission_time") + "</td>");
                
                // Display answer column and a form to submit answers
                out.println("<td>");
                out.println("<form action='SubmitAnswerServlet' method='post'>");
                out.println("<input type='hidden' name='doubtId' value='" + rs.getInt("doubt_id") + "'>");
                out.println("<input type='hidden' name='studentRollNo' value='" + rs.getString("student_roll_no") + "'>");
                out.println("<input type='hidden' name='doubtText' value='" + rs.getString("doubt_text") + "'>");
                out.println("<input type='hidden' name='audioPath' value='" + rs.getString("audio_path") + "'>");
                out.println("<textarea name='answer' rows='3' cols='30'></textarea><br>");
                out.println("<input type='submit' value='Submit Answer'>");
                out.println("</form>");
                out.println("</td>");
                
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</div>");
            out.println("</body></html>");

            con.close();
        } catch (Exception e) {
            out.println("<p>Error: " + e.getMessage() + "</p>");
        }

        out.close();
    }
}
