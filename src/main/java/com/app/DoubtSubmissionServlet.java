package com.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/submitDoubt")
@MultipartConfig
public class DoubtSubmissionServlet extends HttpServlet {

    private static final String SAVE_DIR = "audioUploads";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/rfb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Sreej@2005";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String userName = request.getParameter("userName");
        String userID = request.getParameter("userID");
        String userDepartment = request.getParameter("userDepartment");
        String doubtText = request.getParameter("doubtText");

        Part filePart = request.getPart("audioFile");
        String fileName = "";
        String filePath = "";

        if (filePart != null && filePart.getSize() > 0) {
            fileName = generateUniqueFileName(filePart);
            filePath = SAVE_DIR + File.separator + fileName;

            File uploads = new File(getServletContext().getRealPath("") + File.separator + SAVE_DIR);
            if (!uploads.exists()) {
                uploads.mkdirs();
            }

            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, Paths.get(uploads.getAbsolutePath(), fileName), StandardCopyOption.REPLACE_EXISTING);
            }
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql;
            if (!filePath.isEmpty()) {
                sql = "INSERT INTO doubts (student_roll_no, department, doubt_text, audio_path) VALUES (?, ?, ?, ?)";
            } else {
                sql = "INSERT INTO doubts (student_roll_no, department, doubt_text) VALUES (?, ?, ?)";
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, userID);
                stmt.setString(2, userDepartment);
                stmt.setString(3, doubtText);
                if (!filePath.isEmpty()) {
                    stmt.setString(4, filePath);
                }
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ServletException("Database access error", e);
        }

        response.sendRedirect("submissionSuccess.jsp");
    }


    // Generate a unique file name to avoid overwriting existing files
 // Generate a unique file name to avoid overwriting existing files
    private String generateUniqueFileName(Part filePart) {
        String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String extension = "";
        if (!submittedFileName.isEmpty() && submittedFileName.contains(".")) {
            extension = submittedFileName.substring(submittedFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

}
