package com.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.entity.Movie;
import com.movie.entity.Order;
import com.movie.entity.Screening;
import com.movie.mapper.MovieMapper;
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

    public MovieServiceImpl(@Lazy ScreeningService screeningService,
                            @Lazy OrderService orderService) {
        this.screeningService = screeningService;
        this.orderService = orderService;
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
        return page(new Page<>(current, size), wrapper);
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
}
