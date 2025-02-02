package com.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ViewStudentServlet")
public class ViewStudentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String department = request.getParameter("department");

        try {
            // Establish connection to the database
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/rfb", "root", "Sreej@2005");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students WHERE Department='" + department + "'");
            
            out.println("<html><head><title>Student Details</title>");
            out.println("<link rel='stylesheet' type='text/css' href='viewStudent.css'></head><body>");
            out.println("<div class='container'>");
            out.println("<h2>Students in " + department + " Department:</h2>");
            out.println("<table>");
            out.println("<tr><th>Roll Number</th><th>Name</th><th>Year of Joining</th><th>Mobile</th><th>Email</th><th>Gender</th><th>Date of Birth</th><th>Department</th><th>State</th><th>Country</th></tr>");
            
            while(rs.next()) {
                out.println("<tr><td>" + rs.getString("RollNumber") + "</td><td>" + rs.getString("StudentName") + "</td><td>" + rs.getString("YearOfJoining") + "</td><td>" + rs.getString("Mobile") + "</td><td>" + rs.getString("Email") + "</td><td>" + rs.getString("Gender") + "</td><td>" + rs.getString("DateOfBirth") + "</td><td>" + rs.getString("Department") + "</td><td>" + rs.getString("State") + "</td><td>" + rs.getString("Country") + "</td></tr>");
            }
            
            out.println("</table>");
            out.println("</div>");
            out.println("</body></html>");

            con.close();
        } catch(Exception e) {
            out.println("<p>Error: " + e.getMessage() + "</p>");
        }

        out.close();
    }
}
