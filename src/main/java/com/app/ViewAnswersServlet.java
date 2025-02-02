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

@WebServlet("/ViewAnswersServlet")
public class ViewAnswersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String rollNo = request.getParameter("rollNo");

        try {
            // Establish connection to the database
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/rfb", "root", "Sreej@2005");

            // Query to get answered doubts based on roll number
            String sql = "SELECT * FROM answers WHERE student_roll_no = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, rollNo);
            ResultSet rs = stmt.executeQuery();

            // Generate HTML output
            out.println("<html><head><title>Answered Doubts</title></head><body>");
            out.println("<h2>Answered Doubts for Roll Number: " + rollNo + "</h2>");
            out.println("<table border='1'>");
            out.println("<tr><th>Doubt ID</th><th>Doubt Text</th><th>Audio</th><th>Answer</th></tr>");
            
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("doubt_id") + "</td>");
                out.println("<td>" + rs.getString("doubt_text") + "</td>");
                
                // Display link to audio file
                String audioPath = rs.getString("audio_path");
                if (audioPath != null && !audioPath.isEmpty()) {
                    out.println("<td><a href='" + request.getContextPath() + "/PlayAudioServlet?audioPath=" + audioPath + "'>Listen</a></td>");
                } else {
                    out.println("<td>No audio available</td>");
                }
                
                out.println("<td>" + rs.getString("answer") + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</body></html>");

            con.close();
        } catch (Exception e) {
            out.println("<p>Error: " + e.getMessage() + "</p>");
        }

        out.close();
    }
}
