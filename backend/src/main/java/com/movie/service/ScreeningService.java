package com.movie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.entity.Screening;

import java.util.List;

public interface ScreeningService extends IService<Screening> {

    List<Screening> listByMovieId(Integer movieId);

    Page<Screening> pageScreenings(int current, int size, Integer movieId, String keyword, String hallNumber);

    void deleteScreeningWithCheck(Integer id);
}
