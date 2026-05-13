package com.movie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.movie.common.Result;
import com.movie.entity.Review;
import com.movie.entity.User;
import com.movie.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public Result<Page<Review>> list(@RequestParam Integer movieId,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        Page<Review> result = reviewService.pageByMovie(movieId, new Page<>(page, size));
        return Result.success(result);
    }

    @PostMapping
    public Result<Review> create(@RequestBody Map<String, Object> body, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        Integer movieId = (Integer) body.get("movieId");
        Integer rating = (Integer) body.get("rating");
        String content = (String) body.get("content");
        if (movieId == null || rating == null || rating < 1 || rating > 5) {
            return Result.error(400, "参数不合法");
        }
        Review review = new Review();
        review.setUserId(user.getId());
        review.setMovieId(movieId);
        review.setRating(rating);
        review.setContent(content);
        review.setCreateTime(LocalDateTime.now());
        reviewService.save(review);
        review.setUserName(user.getName());
        return Result.success("评论成功", review);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        Review review = reviewService.getById(id);
        if (review == null) {
            return Result.error(404, "评论不存在");
        }
        if (!review.getUserId().equals(user.getId()) && !"admin".equals(user.getRole())) {
            return Result.error(403, "无权删除他人评论");
        }
        reviewService.removeById(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/stats/{movieId}")
    public Result<Map<String, Object>> stats(@PathVariable Integer movieId) {
        Double avgRating = reviewService.lambdaQuery()
                .eq(Review::getMovieId, movieId)
                .list()
                .stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        long reviewCount = reviewService.lambdaQuery()
                .eq(Review::getMovieId, movieId)
                .count();
        Map<String, Object> stats = new HashMap<>();
        stats.put("avgRating", Math.round(avgRating * 10.0) / 10.0);
        stats.put("reviewCount", reviewCount);
        return Result.success(stats);
    }
}
