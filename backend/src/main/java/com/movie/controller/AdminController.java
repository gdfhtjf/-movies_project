package com.movie.controller;

import com.movie.common.Result;
import com.movie.entity.AdminStatsVO;
import com.movie.entity.Order;
import com.movie.service.MovieService;
import com.movie.service.OrderService;
import com.movie.service.ScreeningService;
import com.movie.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MovieService movieService;
    private final ScreeningService screeningService;
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("/stats")
    public Result<AdminStatsVO> stats() {
        AdminStatsVO vo = new AdminStatsVO();
        vo.setMovieCount(movieService.count());
        vo.setScreeningCount(screeningService.count());
        vo.setUserCount(userService.count());
        vo.setOrderCount(orderService.count());
        vo.setPaidOrderCount(orderService.lambdaQuery()
                .eq(Order::getStatus, "paid")
                .count());
        return Result.success(vo);
    }

    @GetMapping("/order-trend")
    public Result<List<Map<String, Object>>> orderTrend(@RequestParam(defaultValue = "7") int days) {
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            List<Order> dayOrders = orderService.lambdaQuery()
                    .ge(Order::getCreateTime, startOfDay)
                    .lt(Order::getCreateTime, endOfDay)
                    .list();

            long count = dayOrders.size();
            BigDecimal amount = dayOrders.stream()
                    .map(Order::getTotalPrice)
                    .filter(p -> p != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> point = new LinkedHashMap<>();
            point.put("date", date.format(DateTimeFormatter.ofPattern("M/d")));
            point.put("count", count);
            point.put("amount", amount.doubleValue());
            result.add(point);
        }

        return Result.success(result);
    }

    @GetMapping("/movie-revenue")
    public Result<List<Map<String, Object>>> movieRevenue() {
        Map<Integer, BigDecimal> movieRevenueMap = new LinkedHashMap<>();
        Map<Integer, String> movieTitleMap = new LinkedHashMap<>();

        List<Order> allOrders = orderService.lambdaQuery()
                .in(Order::getStatus, "paid", "TICKETED", "COMPLETED")
                .list();

        for (Order order : allOrders) {
            Integer movieId = order.getMovieId();
            if (movieId == null) continue;
            movieRevenueMap.merge(movieId, order.getTotalPrice() != null ? order.getTotalPrice() : BigDecimal.ZERO, BigDecimal::add);
        }

        movieService.list().forEach(m -> {
            movieTitleMap.put(m.getId(), m.getTitle());
        });

        List<Map<String, Object>> result = movieRevenueMap.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("movieId", entry.getKey());
                    item.put("movieTitle", movieTitleMap.getOrDefault(entry.getKey(), "未知电影"));
                    item.put("revenue", entry.getValue().doubleValue());
                    return item;
                })
                .sorted(Comparator.<Map<String, Object>, Double>comparing(
                        m -> (Double) m.get("revenue")).reversed())
                .limit(5)
                .collect(Collectors.toList());

        return Result.success(result);
    }

    @GetMapping("/today-data")
    public Result<Map<String, Object>> todayData() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        List<Order> todayOrders = orderService.lambdaQuery()
                .ge(Order::getCreateTime, startOfDay)
                .lt(Order::getCreateTime, endOfDay)
                .list();

        BigDecimal revenue = todayOrders.stream()
                .map(Order::getTotalPrice)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderCount", todayOrders.size());
        result.put("revenue", revenue.doubleValue());
        return Result.success(result);
    }
}
