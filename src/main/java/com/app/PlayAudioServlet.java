package com.app;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/PlayAudioServlet")
public class PlayAudioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String audioPath = request.getParameter("audioPath");
        if (audioPath != null) {
            try {
                Path file = Paths.get(audioPath);
                Files.copy(file, response.getOutputStream());
                response.setContentType("audio/mpeg");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
