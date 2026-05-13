package com.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.entity.Genre;
import com.movie.entity.Movie;
import com.movie.entity.MovieGenre;
import com.movie.entity.Order;
import com.movie.entity.Screening;
import com.movie.mapper.MovieGenreMapper;
import com.movie.mapper.MovieMapper;
import com.movie.service.GenreService;
import com.movie.service.MovieService;
import com.movie.service.OrderService;
import com.movie.service.ScreeningService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl extends ServiceImpl<MovieMapper, Movie> implements MovieService {

    private final ScreeningService screeningService;
    private final OrderService orderService;
    private final GenreService genreService;
    private final MovieGenreMapper movieGenreMapper;

    public MovieServiceImpl(@Lazy ScreeningService screeningService,
                            @Lazy OrderService orderService,
                            @Lazy GenreService genreService,
                            @Lazy MovieGenreMapper movieGenreMapper) {
        this.screeningService = screeningService;
        this.orderService = orderService;
        this.genreService = genreService;
        this.movieGenreMapper = movieGenreMapper;
    }

    @Override
    public Page<Movie> pageMovies(int current, int size, String keyword,
                                  String genre, BigDecimal minPrice, BigDecimal maxPrice,
                                  LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<Movie> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Movie::getTitle, keyword)
                    .or().like(Movie::getDirector, keyword)
                    .or().like(Movie::getCast, keyword));
        }
        if (StringUtils.hasText(genre)) {
            wrapper.like(Movie::getGenre, genre);
        }
        if (minPrice != null) {
            wrapper.ge(Movie::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(Movie::getPrice, maxPrice);
        }
        if (startDate != null) {
            wrapper.ge(Movie::getReleaseDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(Movie::getReleaseDate, endDate);
        }
        wrapper.orderByDesc(Movie::getCreateTime);
        Page<Movie> result = page(new Page<>(current, size), wrapper);
        fillGenreNames(result.getRecords());
        return result;
    }

    @Override
    @Transactional
    public void deleteMovieWithCheck(Integer id) {
        Movie movie = getById(id);
        if (movie == null) {
            throw new RuntimeException("电影不存在");
        }

        List<Screening> screenings = screeningService.lambdaQuery()
                .eq(Screening::getMovieId, id)
                .list();

        if (!screenings.isEmpty()) {
            List<Integer> screeningIds = screenings.stream()
                    .map(Screening::getId)
                    .collect(Collectors.toList());

            // 先删除该电影所有场次的订单
            orderService.lambdaQuery()
                    .in(Order::getScreeningId, screeningIds)
                    .list()
                    .forEach(order -> orderService.removeById(order.getId()));

            // 再删除该电影的所有场次
            screeningIds.forEach(screeningService::removeById);
        }

        // 最后删除电影
        removeById(id);
    }

    private void fillGenreNames(List<Movie> movies) {
        if (movies.isEmpty()) {
            return;
        }
        java.util.Set<Integer> movieIds = movies.stream()
                .map(Movie::getId)
                .collect(java.util.stream.Collectors.toSet());
        java.util.List<MovieGenre> allMappings = movieGenreMapper.selectList(
                new LambdaQueryWrapper<MovieGenre>().in(MovieGenre::getMovieId, movieIds));
        java.util.Set<Integer> allGenreIds = allMappings.stream()
                .map(MovieGenre::getGenreId)
                .collect(java.util.stream.Collectors.toSet());
        final java.util.Map<Integer, String> genreNameMap;
        if (!allGenreIds.isEmpty()) {
            genreNameMap = genreService.listByIds(allGenreIds).stream()
                    .collect(java.util.stream.Collectors.toMap(Genre::getId, Genre::getName));
        } else {
            genreNameMap = java.util.Map.of();
        }
        java.util.Map<Integer, java.util.List<String>> movieGenreMap = allMappings.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        MovieGenre::getMovieId,
                        java.util.stream.Collectors.mapping(
                                mg -> genreNameMap.getOrDefault(mg.getGenreId(), ""),
                                java.util.stream.Collectors.toList())));
        for (Movie movie : movies) {
            movie.setGenreNames(movieGenreMap.getOrDefault(movie.getId(), java.util.List.of()));
        }
    }
}
