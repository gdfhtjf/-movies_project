package com.movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.entity.HallLayout;

public interface HallLayoutService extends IService<HallLayout> {
    HallLayout getLayoutByHallNumber(String hallNumber);
}
