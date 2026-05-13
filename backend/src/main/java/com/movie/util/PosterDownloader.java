package com.movie.util;

import com.movie.entity.Movie;
import com.movie.mapper.MovieMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PosterDownloader {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final MovieMapper movieMapper;

    private static final Map<String, String> MOVIE_POSTER_MAP = new HashMap<>();

    static {
        MOVIE_POSTER_MAP.put("流浪地球2", "https://picsum.photos/id/1006/600/857");
        MOVIE_POSTER_MAP.put("满江红", "https://picsum.photos/id/1040/600/857");
        MOVIE_POSTER_MAP.put("长津湖", "https://picsum.photos/id/1071/600/857");
        MOVIE_POSTER_MAP.put("你好，李焕英", "https://picsum.photos/id/1074/600/857");
        MOVIE_POSTER_MAP.put("唐人街探案3", "https://picsum.photos/id/1084/600/857");
        MOVIE_POSTER_MAP.put("我不是药神", "https://picsum.photos/id/1036/600/857");
        MOVIE_POSTER_MAP.put("哪吒之魔童降世", "https://picsum.photos/id/1015/600/857");
        MOVIE_POSTER_MAP.put("疯狂的外星人", "https://picsum.photos/id/1025/600/857");
        MOVIE_POSTER_MAP.put("飞驰人生", "https://picsum.photos/id/1031/600/857");
        MOVIE_POSTER_MAP.put("红海行动", "https://picsum.photos/id/1048/600/857");
        MOVIE_POSTER_MAP.put("战狼2", "https://picsum.photos/id/1060/600/857");
        MOVIE_POSTER_MAP.put("万里归途", "https://picsum.photos/id/1069/600/857");
        MOVIE_POSTER_MAP.put("消失的她", "https://picsum.photos/id/1082/600/857");
        MOVIE_POSTER_MAP.put("孤注一掷", "https://picsum.photos/id/1083/600/857");
        MOVIE_POSTER_MAP.put("封神第一部", "https://picsum.photos/id/1080/600/857");
        MOVIE_POSTER_MAP.put("流浪地球3", "https://picsum.photos/id/1019/600/857");
        MOVIE_POSTER_MAP.put("飞驰人生3", "https://picsum.photos/id/1031/600/857");
    }

    public PosterDownloader(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    public void downloadAllPosters() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("创建上传目录: {}", uploadDir);
            }

            List<Movie> movies = movieMapper.selectList(null);
            log.info("开始下载 {} 部电影的海报", movies.size());

            int successCount = 0;
            int failCount = 0;

            for (Movie movie : movies) {
                String posterUrl = MOVIE_POSTER_MAP.get(movie.getTitle());
                if (posterUrl != null) {
                    try {
                        String filename = movie.getPosterPath();
                        if (filename == null || filename.isEmpty()) {
                            filename = sanitizeFilename(movie.getTitle()) + ".jpg";
                        }

                        Path filePath = uploadPath.resolve(filename);
                        if (Files.exists(filePath)) {
                            log.info("海报已存在，跳过: {}", movie.getTitle());
                            successCount++;
                            continue;
                        }

                        boolean downloaded = downloadFileWithRetry(posterUrl, filePath.toAbsolutePath().toString(), 3);

                        if (downloaded) {
                            movie.setPosterPath(filename);
                            movieMapper.updateById(movie);
                            successCount++;
                            log.info("成功下载海报: {} -> {}", movie.getTitle(), filename);
                        } else {
                            failCount++;
                            log.warn("下载海报失败: {}", movie.getTitle());
                        }
                    } catch (Exception e) {
                        failCount++;
                        log.error("下载海报异常: {}, 错误: {}", movie.getTitle(), e.getMessage());
                    }
                }
            }

            log.info("海报下载完成: 成功 {} 张, 失败 {} 张", successCount, failCount);
        } catch (Exception e) {
            log.error("下载海报时发生错误", e);
        }
    }

    private boolean downloadFileWithRetry(String urlStr, String savePath, int maxRetries) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                if (downloadFile(urlStr, savePath)) {
                    return true;
                }
                log.debug("下载尝试 {} 失败，等待后重试...", attempt);
                Thread.sleep(1000 * attempt);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    private boolean downloadFile(String urlStr, String savePath) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            conn.setRequestProperty("Accept", "image/jpeg,image/png,image/webp");
            conn.setInstanceFollowRedirects(true);

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                log.warn("HTTP请求失败: {} - {}", responseCode, urlStr);
                return false;
            }

            try (InputStream in = conn.getInputStream();
                 FileOutputStream out = new FileOutputStream(savePath)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytes = 0;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }

                if (totalBytes > 0) {
                    log.debug("文件下载完成: {}, 大小: {} bytes", savePath, totalBytes);
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            log.error("下载文件失败: {}, 错误: {}", urlStr, e.getMessage());
            return false;
        }
    }

    private String sanitizeFilename(String title) {
        return title.replaceAll("[\\\\/:*?\"<>|]", "-").replaceAll("\\s+", "-");
    }

    public static Map<String, String> getMoviePosterMap() {
        return MOVIE_POSTER_MAP;
    }
}
