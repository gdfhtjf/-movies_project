package com.movie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.movie.common.Result;
import com.movie.entity.Order;
import com.movie.entity.User;
import com.movie.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<Order> create(@RequestBody Map<String, Object> body, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Integer screeningId = (Integer) body.get("screeningId");
        @SuppressWarnings("unchecked")
        List<String> seatNumbers = (List<String>) body.get("seatNumbers");
        Order order = orderService.createOrder(user.getId(), screeningId, seatNumbers);
        return Result.success("下单成功", order);
    }

    @GetMapping
    public Result<Page<Order>> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    HttpSession session) {
        User user = (User) session.getAttribute("user");
        return Result.success(orderService.pageMyOrders(user.getId(), page, size));
    }

    @GetMapping("/my")
    public Result<Page<Order>> myOrders(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        HttpSession session) {
        User user = (User) session.getAttribute("user");
        return Result.success(orderService.pageMyOrders(user.getId(), page, size));
    }

    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        return Result.success(order);
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Integer id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        orderService.cancelOrder(id, user.getId());
        return Result.success("取消成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        orderService.removeById(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/{id}")
    public Result<Order> updateStatus(@PathVariable Integer id,
                                      @RequestBody Map<String, String> body) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        String status = body.get("status");
        if (status == null || status.isEmpty()) {
            return Result.error(400, "状态不能为空");
        }
        order.setStatus(status);
        orderService.updateById(order);
        return Result.success("状态更新成功", order);
    }

    @GetMapping("/admin")
    public Result<Page<Order>> adminOrders(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) String status) {
        return Result.success(orderService.pageAdminOrders(page, size, keyword, status));
    }

    @GetMapping("/screening/{screeningId}/seats")
    public Result<List<String>> occupiedSeats(@PathVariable Integer screeningId) {
        List<Order> orders = orderService.lambdaQuery()
                .eq(Order::getScreeningId, screeningId)
                .eq(Order::getStatus, "paid")
                .list();
        List<String> seats = orders.stream()
                .map(Order::getSeatNumber)
                .flatMap(s -> Arrays.stream(s.split(",")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        return Result.success(seats);
    }
}
