package com.movie.config;

import com.movie.util.PosterDownloader;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PosterAutoDownloadConfig {

    private final PosterDownloader posterDownloader;

    // 设置为true时启动时自动下载海报
    private static final boolean AUTO_DOWNLOAD = true;

    public PosterAutoDownloadConfig(PosterDownloader posterDownloader) {
        this.posterDownloader = posterDownloader;
    }

    @PostConstruct
    public void init() {
        if (AUTO_DOWNLOAD) {
            log.info("========== 开始自动下载电影海报 ==========");
            posterDownloader.downloadAllPosters();
            log.info("========== 海报下载任务完成 ==========");
        }
    }
}
