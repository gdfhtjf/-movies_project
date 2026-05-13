package com.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.entity.Favorite;
import com.movie.entity.Movie;
import com.movie.mapper.FavoriteMapper;
import com.movie.service.FavoriteService;
import com.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final MovieService movieService;

    @Override
    public Page<Movie> getUserFavorites(Integer userId, int page, int size) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.orderByDesc(Favorite::getCreateTime);
        
        Page<Favorite> favoritePage = this.page(new Page<>(page, size), wrapper);
        
        Page<Movie> moviePage = new Page<>(page, size, favoritePage.getTotal());
        List<Movie> movies = new ArrayList<>();
        
        for (Favorite favorite : favoritePage.getRecords()) {
            Movie movie = movieService.getById(favorite.getMovieId());
            if (movie != null) {
                movies.add(movie);
            }
        }
        
        moviePage.setRecords(movies);
        return moviePage;
    }

    @Override
    public boolean isFavorite(Integer userId, Integer movieId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.eq(Favorite::getMovieId, movieId);
        return this.count(wrapper) > 0;
    }

    @Override
    public void addFavorite(Integer userId, Integer movieId) {
        if (!isFavorite(userId, movieId)) {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setMovieId(movieId);
            favorite.setCreateTime(LocalDateTime.now());
            this.save(favorite);
        }
    }

    @Override
    public void removeFavorite(Integer userId, Integer movieId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId);
        wrapper.eq(Favorite::getMovieId, movieId);
        this.remove(wrapper);
    }
}
