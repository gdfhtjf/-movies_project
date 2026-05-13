package com.movie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.movie.common.Result;
import com.movie.entity.Movie;
import com.movie.entity.User;
import com.movie.service.FavoriteService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public Result<Page<Movie>> listMyFavorites(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "20") int size,
                                                HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        Page<Movie> movies = favoriteService.getUserFavorites(user.getId(), page, size);
        return Result.success(movies);
    }

    @GetMapping("/{movieId}/check")
    public Result<Boolean> checkFavorite(@PathVariable Integer movieId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.success(false);
        }
        boolean isFavorite = favoriteService.isFavorite(user.getId(), movieId);
        return Result.success(isFavorite);
    }

    @PostMapping("/{movieId}")
    public Result<Void> addFavorite(@PathVariable Integer movieId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        favoriteService.addFavorite(user.getId(), movieId);
        return Result.success("收藏成功", null);
    }

    @DeleteMapping("/{movieId}")
    public Result<Void> removeFavorite(@PathVariable Integer movieId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        favoriteService.removeFavorite(user.getId(), movieId);
        return Result.success("取消收藏成功", null);
    }
}
