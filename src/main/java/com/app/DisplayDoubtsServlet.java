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


@WebServlet("/displayDoubts")
public class DisplayDoubtsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String rollNo = request.getParameter("rollNo");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Answered Doubts</title>");
        out.println("<link rel='stylesheet' href='answered_doubts.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println("<header>");
        out.println("<h1>Answered Doubts</h1>");
        out.println("</header>");
        out.println("<div class='container'>");
        out.println("<main>");
        out.println("<h2>Your Answered Doubts:</h2>");

        try {
            // Load the database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish a connection
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/rfb", "root", "Sreej@2005");
            // Prepare the SQL query
            String query = "SELECT * FROM answers WHERE student_roll_no = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, rollNo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int doubtId = rs.getInt("doubt_id");
                String doubtText = rs.getString("doubt_text");
                String answer = rs.getString("answer");
                String audioPath = rs.getString("audio_path");

                out.println("<div class='doubt'>");
                out.println("<h3>Doubt ID " + doubtId + "</h3>");
                out.println("<p>" + doubtText + "</p>");
                out.println("<p><b>Answer: </b>" + answer + "</p>");
                out.println("<p><b>Question: </b></p>");
                
                if (audioPath != null && !audioPath.isEmpty()) {
                    out.println("<audio controls>");
                    out.println("<source src='" + audioPath + "' type='audio/mpeg'>");
                    out.println("Your browser does not support the audio element.");
                    out.println("</audio>");
                }
                out.println("</div>");
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Error retrieving data. Please try again later.</p>");
        }

        out.println("</main>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}
