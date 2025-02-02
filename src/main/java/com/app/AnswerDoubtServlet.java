package com.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/answerDoubtServlet")
@MultipartConfig
public class AnswerDoubtServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int doubtId = Integer.parseInt(request.getParameter("doubtId"));
        String answerText = request.getParameter("answerText");
        Part audioAnswer = request.getPart("audioAnswer");

        String audioPath = "";
        if (audioAnswer != null && audioAnswer.getSize() > 0) {
            String fileName = "audioAnswer_" + doubtId + ".mp3";
            audioPath = "uploads/" + fileName;
            File uploads = new File(getServletContext().getRealPath("/") + "uploads");
            if (!uploads.exists()) {
                uploads.mkdir();
            }
            File file = new File(uploads, fileName);
            try (FileOutputStream fos = new FileOutputStream(file); InputStream is = audioAnswer.getInputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
        }

        try {
            // Database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/rfb", "root", "Sreej@2005");

            // Insert answer into the database
            String insertAnswerQuery = "INSERT INTO answers (doubt_id, answer_text, audio_path) VALUES (?, ?, ?)";
            PreparedStatement insertAnswerStmt = conn.prepareStatement(insertAnswerQuery);
            insertAnswerStmt.setInt(1, doubtId);
            insertAnswerStmt.setString(2, answerText);
            insertAnswerStmt.setString(3, audioPath);
            insertAnswerStmt.executeUpdate();

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("viewDoubts.html");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
