package com.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/SendReportServlet")
public class SendReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	System.out.println("Entering dopost methid");
        String parentEmail = request.getParameter("parentEmail");
        String reportOption = request.getParameter("reportOption");
        String rollNo = request.getParameter("rollNo");
        String data = request.getParameter("data");

        // Save the report to the database
        saveReportToDatabase(parentEmail, reportOption, rollNo, data);

        String subject = "Report for Roll No: " + rollNo;
        String messageBody = "";

        if (reportOption.equals("attendance")) {
            messageBody = "Dear parent,\n\nThis is to inform you that your ward with Roll No. " + rollNo + " has attendance of " + data + ".\n\nSincerely,\nThe Mentor";
        } else if (reportOption.equals("marks")) {
            messageBody = "Dear parent,\n\nThis is to inform you that your ward with Roll No. " + rollNo + " has scored " + data + " marks.\n\nSincerely,\nThe Mentor";
        } else if (reportOption.equals("issues")) {
            messageBody = "Dear parent,\n\nThis is to inform you that there are " + data + " issues to report for your ward with Roll No. " + rollNo + ".\n\nSincerely,\nThe Mentor";
        }

        // Sending the email
        String host = "smtp.gmail.com"; // SMTP server hostname
        final String username = "cvrrfb@gmail.com"; // SMTP server username
        final String password = "kjub awva kggu lgot"; // SMTP server password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(parentEmail));
            message.setSubject(subject);
            message.setText(messageBody);
            session.setDebug(true);

            Transport.send(message);
            response.getWriter().write("Email sent successfully");
        } catch (MessagingException e) {
            response.getWriter().write("Failed to send email. Please try again.");
            e.printStackTrace();
        }
    }

    private void saveReportToDatabase(String parentEmail, String reportOption, String rollNo, String data) {
        System.out.println("Inside saveReportToDatabase method");

        String jdbcURL = "jdbc:mysql://localhost:3306/rfb"; // Change to your database URL
        String dbUser = "root"; // Change to your database user
        String dbPassword = "Sreej@2005"; // Change to your database password

        String sql = "INSERT INTO reports (parent_email, report_type, roll_no, data) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            System.out.println("Preparing SQL statement: " + sql);

            statement.setString(1, parentEmail);
            statement.setString(2, reportOption);
            statement.setString(3, rollNo);
            statement.setString(4, data);

            statement.executeUpdate();
            System.out.println("Data inserted into the database successfully");
        } catch (SQLException e) {
            System.out.println("Error occurred while executing SQL statement: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
