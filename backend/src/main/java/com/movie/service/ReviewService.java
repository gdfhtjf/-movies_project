package com.movie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.entity.Review;

public interface ReviewService extends IService<Review> {

    Page<Review> pageByMovie(Integer movieId, Page<Review> page);
}
