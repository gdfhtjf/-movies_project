package com.movie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.movie.common.Result;
import com.movie.entity.Movie;
import com.movie.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Validated
public class MovieController {

    private final MovieService movieService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping
    public Result<Page<Movie>> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String genre,
                                    @RequestParam(required = false) BigDecimal minPrice,
                                    @RequestParam(required = false) BigDecimal maxPrice,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(movieService.pageMovies(page, size, keyword, genre, minPrice, maxPrice, startDate, endDate));
    }

    @GetMapping("/{id}")
    public Result<Movie> detail(@PathVariable Integer id) {
        Movie movie = movieService.getById(id);
        if (movie == null) {
            return Result.error(404, "电影不存在");
        }
        return Result.success(movie);
    }

    @PostMapping
    public Result<Movie> add(@Valid Movie movie, @RequestParam MultipartFile poster) throws IOException {
        String filename = saveFile(poster);
        movie.setPosterPath(filename);
        movie.setCreateTime(LocalDateTime.now());
        movie.setUpdateTime(LocalDateTime.now());
        movieService.save(movie);
        return Result.success("添加成功", movie);
    }

    @PutMapping("/{id}")
    public Result<Movie> update(@PathVariable Integer id,
                                @Valid Movie movie,
                                @RequestParam(required = false) MultipartFile poster) throws IOException {
        Movie existing = movieService.getById(id);
        if (existing == null) {
            return Result.error(404, "电影不存在");
        }
        movie.setId(id);
        if (poster != null && !poster.isEmpty()) {
            deleteFile(existing.getPosterPath());
            movie.setPosterPath(saveFile(poster));
        } else {
            movie.setPosterPath(existing.getPosterPath());
        }
        movie.setTrailerPath(existing.getTrailerPath());
        movie.setCreateTime(existing.getCreateTime());
        movie.setUpdateTime(LocalDateTime.now());
        movieService.updateById(movie);
        return Result.success("更新成功", movie);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        Movie movie = movieService.getById(id);
        if (movie == null) {
            return Result.error(404, "电影不存在");
        }
        movieService.deleteMovieWithCheck(id);
        deleteFile(movie.getPosterPath());
        deleteFile(movie.getTrailerPath());
        return Result.success("删除成功", null);
    }

    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Integer> ids) {
        for (Integer id : ids) {
            Movie movie = movieService.getById(id);
            if (movie != null) {
                movieService.deleteMovieWithCheck(id);
                deleteFile(movie.getPosterPath());
                deleteFile(movie.getTrailerPath());
            }
        }
        return Result.success("批量删除成功", null);
    }

    @PostMapping("/{id}/trailer")
    public Result<Movie> uploadTrailer(@PathVariable Integer id,
                                       @RequestParam MultipartFile trailer) throws IOException {
        Movie movie = movieService.getById(id);
        if (movie == null) {
            return Result.error(404, "电影不存在");
        }
        deleteFile(movie.getTrailerPath());
        movie.setTrailerPath(saveFile(trailer));
        movie.setUpdateTime(LocalDateTime.now());
        movieService.updateById(movie);
        return Result.success("预告片上传成功", movie);
    }

    @DeleteMapping("/{id}/trailer")
    public Result<Void> deleteTrailer(@PathVariable Integer id) {
        Movie movie = movieService.getById(id);
        if (movie == null) {
            return Result.error(404, "电影不存在");
        }
        deleteFile(movie.getTrailerPath());
        movie.setTrailerPath(null);
        movie.setUpdateTime(LocalDateTime.now());
        movieService.updateById(movie);
        return Result.success("预告片删除成功", null);
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
