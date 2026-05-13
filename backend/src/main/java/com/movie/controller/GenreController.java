package com.movie.controller;

import com.movie.common.Result;
import com.movie.entity.Genre;
import com.movie.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public Result<List<Genre>> list() {
        return Result.success(genreService.list());
    }

    @GetMapping("/movie/{movieId}")
    public Result<List<Genre>> getGenresByMovieId(@PathVariable Integer movieId) {
        return Result.success(genreService.getGenresByMovieId(movieId));
    }

    @PutMapping("/movie/{movieId}")
    public Result<List<Genre>> setMovieGenres(@PathVariable Integer movieId,
                                               @RequestBody List<Integer> genreIds) {
        genreService.setMovieGenres(movieId, genreIds);
        return Result.success(genreService.getGenresByMovieId(movieId));
    }
}
