package com.movie.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/download")
    public void download(@RequestParam(required = false) String file, HttpServletResponse response) throws IOException {
        if (file == null || file.isBlank()) {
            response.setStatus(400);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Missing 'file' parameter");
            return;
        }
        
        File target = new File(uploadDir, file);
        if (!target.exists() || target.isDirectory()) {
            response.setStatus(404);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("File not found");
            return;
        }
        
        response.setContentType("application/octet-stream");
        response.setContentLengthLong(target.length());
        String encodedFileName = URLEncoder.encode(file, "UTF-8").replace("+", "%20");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
        try (FileInputStream fis = new FileInputStream(target);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }
    }
}
