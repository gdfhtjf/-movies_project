package com.movie.controller;

import com.movie.common.Result;
import com.movie.entity.Movie;
import com.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posters")
@RequiredArgsConstructor
public class PosterController {

    private final MovieService movieService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping
    public Result<List<Movie>> list() {
        return Result.success(movieService.list());
    }

    @PostMapping("/upload")
    public Result<Movie> upload(@RequestParam Integer movieId,
                                @RequestParam MultipartFile poster) throws IOException {
        Movie movie = movieService.getById(movieId);
        if (movie == null) {
            return Result.error(404, "电影不存在");
        }
        String filename = saveFile(poster);
        movie.setPosterPath(filename);
        movieService.updateById(movie);
        return Result.success("上传成功", movie);
    }

    @PutMapping("/{movieId}")
    public Result<Movie> replace(@PathVariable Integer movieId,
                                 @RequestParam MultipartFile poster) throws IOException {
        Movie movie = movieService.getById(movieId);
        if (movie == null) {
            return Result.error(404, "电影不存在");
        }
        deleteFile(movie.getPosterPath());
        String filename = saveFile(poster);
        movie.setPosterPath(filename);
        movieService.updateById(movie);
        return Result.success("替换成功", movie);
    }

    @DeleteMapping("/{movieId}")
    public Result<Void> delete(@PathVariable Integer movieId) {
        Movie movie = movieService.getById(movieId);
        if (movie == null) {
            return Result.error(404, "电影不存在");
        }
        deleteFile(movie.getPosterPath());
        movie.setPosterPath(null);
        movieService.updateById(movie);
        return Result.success("删除成功", null);
    }

    private String saveFile(MultipartFile file) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
        file.transferTo(new File(dir, filename));
        return filename;
    }

    private void deleteFile(String filename) {
        if (filename != null && !filename.isEmpty()) {
            File file = new File(uploadDir, filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
