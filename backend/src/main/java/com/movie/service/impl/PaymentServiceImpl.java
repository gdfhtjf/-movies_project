package com.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.movie.entity.Order;
import com.movie.mapper.OrderMapper;
import com.movie.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public boolean processPayment(Integer orderId) {
        LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Order::getId, orderId)
               .eq(Order::getStatus, OrderServiceImpl.STATUS_PENDING)
               .set(Order::getStatus, OrderServiceImpl.STATUS_PAID);
        return orderMapper.update(null, wrapper) > 0;
    }
}
