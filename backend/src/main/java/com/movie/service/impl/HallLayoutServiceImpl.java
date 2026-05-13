package com.movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.entity.HallLayout;
import com.movie.mapper.HallLayoutMapper;
import com.movie.service.HallLayoutService;
import org.springframework.stereotype.Service;

@Service
public class HallLayoutServiceImpl extends ServiceImpl<HallLayoutMapper, HallLayout> implements HallLayoutService {

    @Override
    public HallLayout getLayoutByHallNumber(String hallNumber) {
        return lambdaQuery().eq(HallLayout::getHallNumber, hallNumber).one();
    }
}
