package com.movie.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.entity.Movie;
import com.movie.entity.Order;
import com.movie.entity.Screening;
import com.movie.mapper.OrderMapper;
import com.movie.service.MovieService;
import com.movie.service.OrderService;
import com.movie.service.ScreeningService;
import com.movie.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final ScreeningService screeningService;
    private final MovieService movieService;
    private final UserService userService;

    @Override
    public Page<Order> pageMyOrders(Integer userId, int current, int size) {
        Page<Order> page = lambdaQuery()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime)
                .page(new Page<>(current, size));
        fillOrderDetails(page.getRecords());
        return page;
    }

    @Override
    public Page<Order> pageAdminOrders(int current, int size, String keyword, String status) {
        Page<Order> page = lambdaQuery()
                .eq(status != null && !status.isEmpty(), Order::getStatus, status)
                .orderByDesc(Order::getCreateTime)
                .page(new Page<>(current, size));
        
        fillOrderDetails(page.getRecords());
        
        // 内存中过滤keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase().trim();
            List<Order> filtered = page.getRecords().stream()
                    .filter(o -> {
                        // 匹配订单号
                        if (String.valueOf(o.getId()).contains(keyword)) {
                            return true;
                        }
                        // 匹配电影名
                        if (o.getMovieTitle() != null && o.getMovieTitle().toLowerCase().contains(lowerKeyword)) {
                            return true;
                        }
                        // 匹配用户名
                        if (o.getUserName() != null && o.getUserName().toLowerCase().contains(lowerKeyword)) {
                            return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
            page.setRecords(filtered);
            page.setTotal(filtered.size());
        }
        
        return page;
    }

    @Override
    @Transactional
    public Order createOrder(Integer userId, Integer screeningId, List<String> seatNumbers) {
        Screening screening = screeningService.getById(screeningId);
        if (screening == null) {
            throw new RuntimeException("场次不存在");
        }
        if (screening.getRemainingSeats() < seatNumbers.size()) {
            throw new RuntimeException("剩余座位不足，仅剩" + screening.getRemainingSeats() + "个座位");
        }

        Set<String> occupied = lambdaQuery()
                .eq(Order::getScreeningId, screeningId)
                .eq(Order::getStatus, "paid")
                .list()
                .stream()
                .flatMap(o -> Arrays.stream(o.getSeatNumber().split(",")))
                .map(String::trim)
                .collect(Collectors.toSet());

        for (String seat : seatNumbers) {
            if (occupied.contains(seat.trim())) {
                throw new RuntimeException("座位 " + seat.trim() + " 已被占用");
            }
        }

        Movie movie = movieService.getById(screening.getMovieId());
        BigDecimal totalPrice = movie.getPrice().multiply(new BigDecimal(seatNumbers.size()));

        boolean updated = screeningService.lambdaUpdate()
                .eq(Screening::getId, screeningId)
                .eq(Screening::getVersion, screening.getVersion())
                .set(Screening::getRemainingSeats, screening.getRemainingSeats() - seatNumbers.size())
                .set(Screening::getVersion, screening.getVersion() + 1)
                .update();
        if (!updated) {
            throw new RuntimeException("系统繁忙，请重试");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setMovieId(screening.getMovieId());
        order.setScreeningId(screeningId);
        order.setSeatNumber(String.join(",", seatNumbers));
        order.setTotalPrice(totalPrice);
        order.setStatus("paid");
        order.setVersion(1);
        order.setCreateTime(LocalDateTime.now());
        save(order);
        return order;
    }

    @Override
    @Transactional
    public void cancelOrder(Integer orderId, Integer userId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"paid".equals(order.getStatus())) {
            throw new RuntimeException("该订单状态为" + order.getStatus() + "，无法取消");
        }
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作他人订单");
        }

        Screening screening = screeningService.getById(order.getScreeningId());
        if (screening == null) {
            throw new RuntimeException("关联场次不存在");
        }
        if (screening.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("放映已开始，无法取消");
        }

        int seatCount = order.getSeatNumber().split(",").length;

        boolean updated = screeningService.lambdaUpdate()
                .eq(Screening::getId, screening.getId())
                .eq(Screening::getVersion, screening.getVersion())
                .set(Screening::getRemainingSeats, screening.getRemainingSeats() + seatCount)
                .set(Screening::getVersion, screening.getVersion() + 1)
                .update();
        if (!updated) {
            throw new RuntimeException("系统繁忙，取消订单失败，请重试");
        }

        order.setStatus("cancelled");
        order.setCancelTime(LocalDateTime.now());
        updateById(order);
    }

    private void fillOrderDetails(List<Order> orders) {
        for (Order order : orders) {
            Movie movie = movieService.getById(order.getMovieId());
            if (movie != null) {
                order.setMovieTitle(movie.getTitle());
            }
            Screening screening = screeningService.getById(order.getScreeningId());
            if (screening != null) {
                order.setHallNumber(screening.getHallNumber());
                order.setShowTime(screening.getStartTime());
            }
            com.movie.entity.User user = userService.getById(order.getUserId());
            if (user != null) {
                order.setUserName(user.getName());
            }
        }
    }
}
