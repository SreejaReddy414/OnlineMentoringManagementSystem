package com.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/addStudent")
@MultipartConfig(maxFileSize = 16177215) // 16 MB
public class AddStudentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // MySQL database credentials
    private final String dbURL = "jdbc:mysql://localhost:3306/rfb";
    private final String dbUser = "root";
    private final String dbPassword = "Sreej@2005";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Student Details
        String studentName = request.getParameter("studname");
        String rollNumber = request.getParameter("studrollno");
        String yearOfJoining = request.getParameter("studyear");
        String mobile = request.getParameter("studmobile");
        String email = request.getParameter("studemail");
        String gender = request.getParameter("gender");
        String dob = request.getParameter("studdob");
        String department = request.getParameter("studdept");
        String state = request.getParameter("studstate");
        String country = request.getParameter("studcountry");
        InputStream inputStream = null; // input stream of the upload file

        // Log retrieved parameters for debugging
        System.out.println("Retrieved Parameters:");
        System.out.println("RollNumber: " + rollNumber);
        System.out.println("StudentName: " + studentName);
        // Log other parameters similarly...

        // Check if the rollNumber is null or empty
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            request.setAttribute("Message", "Roll Number is required and cannot be null or empty.");
            getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
            return;
        }

        // Obtain the upload file part in this multipart request
        Part filePart = request.getPart("studphoto");
        if (filePart != null) {
            // Obtain input stream of the upload file
            inputStream = filePart.getInputStream();
        }

        // Parent Details
        String motherName = request.getParameter("mothername");
        String motherMobile = request.getParameter("mothermobile");
        String motherEmail = request.getParameter("motheremail");
        String fatherName = request.getParameter("fathername");
        String fatherMobile = request.getParameter("fathermobile");
        String fatherEmail = request.getParameter("fatheremail");

        Connection conn = null; // connection to the database
        String message = null;  // message will be sent back to client

        try {
            // Connects to the database
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);

            // Generate random username and password
            String username = generateRandomUsername(studentName);
            String password = generateRandomPassword();

            // Construct SQL statement for students table
            String studentSql = "INSERT INTO students (RollNumber, StudentName, YearOfJoining, Mobile, Email, Gender, DateOfBirth, Department, State, Country, Photo) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement studentStatement = conn.prepareStatement(studentSql);
            studentStatement.setString(1, rollNumber);
            studentStatement.setString(2, studentName);
            studentStatement.setString(3, yearOfJoining);
            studentStatement.setString(4, mobile);
            studentStatement.setString(5, email);
            studentStatement.setString(6, gender);
            studentStatement.setString(7, dob);
            studentStatement.setString(8, department);
            studentStatement.setString(9, state);
            studentStatement.setString(10, country);

            if (inputStream != null) {
                // Fetches input stream of the upload file for the blob column
                studentStatement.setBlob(11, inputStream);
            } else {
                // If no image is uploaded, set the blob parameter to null or some default value
                studentStatement.setNull(11, java.sql.Types.BLOB);
            }

            // Send the statement to the database server
            int studentRow = studentStatement.executeUpdate();

            // Construct SQL statement for users table
            String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement userStatement = conn.prepareStatement(userSql);
            userStatement.setString(1, username);
            userStatement.setString(2, password);
            userStatement.setString(3, "mentee");

            // Send the statement to the database server
            int userRow = userStatement.executeUpdate();

            // Construct SQL statement for parents table
            String parentSql = "INSERT INTO parents (RollNumber, MotherName, MotherMobile, MotherEmail, FatherName, FatherMobile, FatherEmail) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement parentStatement = conn.prepareStatement(parentSql);
            parentStatement.setString(1, rollNumber);
            parentStatement.setString(2, motherName);
            parentStatement.setString(3, motherMobile);
            parentStatement.setString(4, motherEmail);
            parentStatement.setString(5, fatherName);
            parentStatement.setString(6, fatherMobile);
            parentStatement.setString(7, fatherEmail);

            // Send the statement to the database server
            int parentRow = parentStatement.executeUpdate();

            // Create directory for student credentials
            File dir = new File("C:\\student_credentials");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Write credentials to file
            File file = new File(dir, rollNumber + ".txt");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                String content = "Username: " + username + "\nPassword: " + password;
                fos.write(content.getBytes());
            }

            if (studentRow > 0 && userRow > 0 && parentRow > 0) {
                message = "Student details and credentials added successfully!";
            }
        } catch (SQLException ex) {
            message = "ERROR: " + ex.getMessage();
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found");
            e.printStackTrace();
        } finally {
            if (conn != null) {
                // Closes the database connection
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            // Sets the message in request scope
            request.setAttribute("Message", message);

            // Forwards to the message page
            getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
        }
    }

    private String generateRandomUsername(String studentName) {
        Random rand = new Random();
        String randomDigits = String.format("%03d", rand.nextInt(1000));
        return studentName.replaceAll("\\s", "") + randomDigits;
    }

    private String generateRandomPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rand = new Random();
        StringBuilder password = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            password.append(characters.charAt(rand.nextInt(characters.length())));
        }
        return password.toString();
    }
}
