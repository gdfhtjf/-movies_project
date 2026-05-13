package com.movie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.entity.Movie;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface MovieService extends IService<Movie> {

    Page<Movie> pageMovies(int current, int size, String keyword,
                           String genre, BigDecimal minPrice, BigDecimal maxPrice,
                           LocalDate startDate, LocalDate endDate);

    void deleteMovieWithCheck(Integer id);
}
