package com.movie.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.nio.file.Files;

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

    @GetMapping("/stream/{fileName:.+}")
    public void stream(@PathVariable String fileName,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
        File target = new File(uploadDir, fileName);
        if (!target.exists() || target.isDirectory()) {
            response.setStatus(404);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("File not found");
            return;
        }

        long fileLength = target.length();
        String contentType = Files.probeContentType(target.toPath());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        String rangeHeader = request.getHeader("Range");
        if (rangeHeader == null || !rangeHeader.startsWith("bytes=")) {
            // 无 Range 请求，返回完整文件
            response.setContentType(contentType);
            response.setContentLengthLong(fileLength);
            response.setHeader("Accept-Ranges", "bytes");
            try (FileInputStream fis = new FileInputStream(target);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }
            return;
        }

        // 解析 Range 头
        String rangeValue = rangeHeader.substring(6); // 去掉 "bytes="
        long start, end;
        int dashIdx = rangeValue.indexOf('-');
        if (dashIdx == 0) {
            // 形式: bytes=-500 (最后500字节)
            long suffix = Long.parseLong(rangeValue.substring(1));
            start = Math.max(fileLength - suffix, 0);
            end = fileLength - 1;
        } else if (dashIdx == rangeValue.length() - 1) {
            // 形式: bytes=500-
            start = Long.parseLong(rangeValue.substring(0, dashIdx));
            end = fileLength - 1;
        } else {
            // 形式: bytes=500-999
            start = Long.parseLong(rangeValue.substring(0, dashIdx));
            end = Math.min(Long.parseLong(rangeValue.substring(dashIdx + 1)), fileLength - 1);
        }

        if (start >= fileLength) {
            response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            response.setHeader("Content-Range", "bytes */" + fileLength);
            return;
        }

        long contentLength = end - start + 1;
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setContentType(contentType);
        response.setContentLengthLong(contentLength);
        response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
        response.setHeader("Accept-Ranges", "bytes");

        try (RandomAccessFile raf = new RandomAccessFile(target, "r");
             OutputStream os = response.getOutputStream()) {
            raf.seek(start);
            byte[] buffer = new byte[8192];
            long remaining = contentLength;
            int bytesRead;
            while (remaining > 0 && (bytesRead = raf.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                os.write(buffer, 0, bytesRead);
                remaining -= bytesRead;
            }
            os.flush();
        }
    }
}
