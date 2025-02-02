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

@WebServlet("/ViewStaffServlet")
public class ViewStaffServlet extends HttpServlet {
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
            ResultSet rs = stmt.executeQuery("SELECT * FROM mentor WHERE staff_dept='" + department + "'");
            
            out.println("<html><head><title>Staff Details</title>");
            out.println("<link rel='stylesheet' type='text/css' href='viewStaff.css'></head><body>");
            out.println("<div class='container'>");
          out.println("<h2>Staff in " + department + " Department:</h2>");
            out.println("<table>");
            out.println("<tr><th>Employee ID</th><th>Name</th><th>Mobile</th><th>Email</th><th>Gender</th><th>Department</th><th>Date of Birth</th><th>State</th><th>Country</th></tr>");
            
            while(rs.next()) {
                out.println("<tr><td>" + rs.getString("employee_id") + "</td><td>" + rs.getString("staff_name") + "</td><td>" + rs.getString("staff_mobile") + "</td><td>" + rs.getString("staff_email") + "</td><td>" + rs.getString("gender") + "</td><td>" + rs.getString("staff_dept") + "</td><td>" + rs.getString("staff_dob") + "</td><td>" + rs.getString("staff_state") + "</td><td>" + rs.getString("staff_country") + "</td></tr>");
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
