package com.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.entity.Movie;
import com.movie.entity.Order;
import com.movie.entity.Screening;
import com.movie.mapper.ScreeningMapper;
import com.movie.service.MovieService;
import com.movie.service.OrderService;
import com.movie.service.ScreeningService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScreeningServiceImpl extends ServiceImpl<ScreeningMapper, Screening> implements ScreeningService {

    private final MovieService movieService;
    private final OrderService orderService;

    public ScreeningServiceImpl(@Lazy MovieService movieService,
                                @Lazy OrderService orderService) {
        this.movieService = movieService;
        this.orderService = orderService;
    }

    @Override
    public List<Screening> listByMovieId(Integer movieId) {
        LambdaQueryWrapper<Screening> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Screening::getMovieId, movieId)
               .orderByAsc(Screening::getStartTime);
        List<Screening> screenings = list(wrapper);
        fillMovieData(screenings);
        return screenings;
    }

    private void fillMovieData(List<Screening> screenings) {
        if (screenings.isEmpty()) {
            return;
        }
        Set<Integer> movieIds = screenings.stream()
                .map(Screening::getMovieId)
                .collect(Collectors.toSet());
        Map<Integer, Movie> movieMap = movieService.listByIds(movieIds).stream()
                .collect(Collectors.toMap(Movie::getId, m -> m));
        for (Screening s : screenings) {
            Movie movie = movieMap.get(s.getMovieId());
            if (movie != null) {
                s.setMovieTitle(movie.getTitle());
                if (s.getPrice() == null) {
                    s.setPrice(movie.getPrice());
                }
            }
            if (s.getShowDate() == null && s.getStartTime() != null) {
                s.setShowDate(s.getStartTime());
            }
        }
    }

    @Override
    public Page<Screening> pageScreenings(int current, int size, Integer movieId, String keyword, String hallNumber) {
        LambdaQueryWrapper<Screening> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            List<Integer> movieIds = movieService.lambdaQuery()
                    .like(Movie::getTitle, keyword)
                    .list()
                    .stream()
                    .map(Movie::getId)
                    .collect(Collectors.toList());
            if (!movieIds.isEmpty()) {
                wrapper.in(Screening::getMovieId, movieIds);
            } else {
                Page<Screening> emptyPage = new Page<>(current, size);
                emptyPage.setRecords(List.of());
                emptyPage.setTotal(0);
                return emptyPage;
            }
        }

        if (movieId != null) {
            wrapper.eq(Screening::getMovieId, movieId);
        }
        if (StringUtils.hasText(hallNumber)) {
            wrapper.like(Screening::getHallNumber, hallNumber);
        }

        wrapper.orderByAsc(Screening::getStartTime);
        Page<Screening> page = page(new Page<>(current, size), wrapper);
        fillMovieData(page.getRecords());
        return page;
    }

    @Override
    public boolean hasTimeConflict(String hallNumber, LocalDateTime startTime, LocalDateTime endTime, Integer excludeScreeningId) {
        LambdaQueryWrapper<Screening> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Screening::getHallNumber, hallNumber)
               .lt(Screening::getStartTime, endTime)
               .gt(Screening::getEndTime, startTime);
        if (excludeScreeningId != null) {
            wrapper.ne(Screening::getId, excludeScreeningId);
        }
        return count(wrapper) > 0;
    }

    @Override
    @Transactional
    public void deleteScreeningWithCheck(Integer id) {
        Screening screening = getById(id);
        if (screening == null) {
            throw new RuntimeException("场次不存在");
        }

        orderService.lambdaQuery()
                .eq(Order::getScreeningId, id)
                .list()
                .forEach(order -> orderService.removeById(order.getId()));

        removeById(id);
    }

}
