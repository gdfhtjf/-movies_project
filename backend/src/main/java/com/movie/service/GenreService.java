package com.movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.entity.Genre;

import java.util.List;

public interface GenreService extends IService<Genre> {

    List<Genre> getGenresByMovieId(Integer movieId);

    void setMovieGenres(Integer movieId, List<Integer> genreIds);
}
