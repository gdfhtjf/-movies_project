package com.movie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.entity.Favorite;
import com.movie.entity.Movie;

public interface FavoriteService extends IService<Favorite> {

    Page<Movie> getUserFavorites(Integer userId, int page, int size);

    boolean isFavorite(Integer userId, Integer movieId);

    void addFavorite(Integer userId, Integer movieId);

    void removeFavorite(Integer userId, Integer movieId);
}
