package com.movie.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.entity.Review;
import com.movie.entity.User;
import com.movie.mapper.ReviewMapper;
import com.movie.service.ReviewService;
import com.movie.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    private final UserService userService;

    @Override
    public Page<Review> pageByMovie(Integer movieId, Page<Review> page) {
        Page<Review> result = lambdaQuery()
                .eq(Review::getMovieId, movieId)
                .orderByDesc(Review::getCreateTime)
                .page(page);
        fillUserName(result.getRecords());
        return result;
    }

    private void fillUserName(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return;
        }
        Set<Integer> userIds = reviews.stream()
                .map(Review::getUserId)
                .collect(Collectors.toSet());
        Map<Integer, String> userMap = userService.listByIds(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, User::getName));
        for (Review review : reviews) {
            review.setUserName(userMap.getOrDefault(review.getUserId(), "未知用户"));
        }
    }
}
