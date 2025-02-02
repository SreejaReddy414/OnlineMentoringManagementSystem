package com.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/AddStaff")
@MultipartConfig(maxFileSize = 16177215) // 16MB
public class AddStaffServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Entering doPost method");
        
        String employeeId = request.getParameter("employeeid");
        String name = request.getParameter("staffname");
        String mobile = request.getParameter("staffmobile");
        String email = request.getParameter("staffemail");
        String gender = request.getParameter("gender");
        String dob = request.getParameter("staffdob");
        String department = request.getParameter("staffdept");
        String state = request.getParameter("staffstate");
        String country = request.getParameter("staffcountry");
        Part filePart = request.getPart("staffphoto");

        InputStream photoInputStream = null;
        if (filePart != null) {
            photoInputStream = filePart.getInputStream();
        }

        Connection conn = null;
        String message = null;

        try {
            String url = "jdbc:mysql://localhost:3306/rfb";
            String user = "root";
            String password = "Sreej@2005";

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);

            System.out.println("Connected to the database");

            // Insert staff information
            String sql = "INSERT INTO mentor (employee_id, staff_name, staff_mobile, staff_email, gender, staff_dob, staff_dept, staff_state, staff_country, staff_photo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, employeeId);
            statement.setString(2, name);
            statement.setString(3, mobile);
            statement.setString(4, email);
            statement.setString(5, gender);
            statement.setString(6, dob);
            statement.setString(7, department);
            statement.setString(8, state);
            statement.setString(9, country);

            if (photoInputStream != null) {
                statement.setBlob(10, photoInputStream);
            }

            int row = statement.executeUpdate();
            if (row > 0) {
                System.out.println("Staff record inserted");

                // Generate random username and password
                String username = generateRandomUsername(name);
                String passwordGenerated = generateRandomPassword();

                System.out.println("Generated username: " + username);
                System.out.println("Generated password: " + passwordGenerated);

                // Insert username, password, and role into users table
                String userSql = "INSERT INTO users (username, password, role, created_at) VALUES (?, ?, 'mentor', CURRENT_TIMESTAMP)";
                PreparedStatement userStatement = conn.prepareStatement(userSql);
                userStatement.setString(1, username);
                userStatement.setString(2, passwordGenerated);

                int userRow = userStatement.executeUpdate();
                if (userRow > 0) {
                    System.out.println("User credentials inserted");

                    // Write username and password to a file on C: drive
                    writeCredentialsToFile(employeeId, username, passwordGenerated);

                    message = "Staff added, and user credentials generated.";
                } else {
                    message = "Staff added, but failed to generate user credentials.";
                }
            } else {
                message = "Failed to add staff.";
            }
        } catch (SQLException | ClassNotFoundException ex) {
            message = "ERROR: " + ex.getMessage();
            ex.printStackTrace();
        } catch (Exception ex) {
            message = "Unexpected ERROR: " + ex.getMessage();
            ex.printStackTrace();
        } finally {
            System.out.println("Setting message: " + message);
            if (message == null) {
                message = "Unexpected error occurred.";
            }
            request.setAttribute("Message", message);
            getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Database connection closed");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private String generateRandomUsername(String name) {
        String username = name.replaceAll("\\s", "").toLowerCase(); // Remove spaces and convert to lowercase
        Random random = new Random();
        int randomDigits = random.nextInt(900) + 100; // Generate 3 random digits between 100 and 999
        return username + randomDigits;
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private void writeCredentialsToFile(String employeeId, String username, String password) {
        String directoryPath = "C:\\staff_credentials";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        
        String filePath = directoryPath + "\\" + employeeId + ".txt";
        try (OutputStream os = new FileOutputStream(filePath)) {
            String credentials = "Username: " + username + "\nPassword: " + password + "\n";
            os.write(credentials.getBytes());
            System.out.println("Credentials written to file: " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing credentials to file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
