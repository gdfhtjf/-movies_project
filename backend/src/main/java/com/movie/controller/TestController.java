package com.movie.controller;

import com.movie.common.Result;
import com.movie.entity.Movie;
import com.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final MovieService movieService;

    @GetMapping("/movie-count")
    public Result<Long> getMovieCount() {
        long count = movieService.count();
        System.out.println("数据库中电影总数: " + count);
        return Result.success(count);
    }

    @GetMapping("/all-movies")
    public Result<List<Movie>> getAllMovies() {
        List<Movie> list = movieService.list();
        System.out.println("查询到 " + list.size() + " 部电影");
        return Result.success(list);
    }
}
