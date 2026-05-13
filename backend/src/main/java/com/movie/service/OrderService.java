package com.movie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.entity.Order;

import java.util.List;

public interface OrderService extends IService<Order> {

    Order createOrder(Integer userId, Integer screeningId, List<String> seatNumbers);

    void cancelOrder(Integer orderId, Integer userId);

    Page<Order> pageMyOrders(Integer userId, int current, int size);

    Page<Order> pageAdminOrders(int current, int size, String keyword, String status);
}
