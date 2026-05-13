package com.movie.controller;

import com.movie.entity.Movie;
import com.movie.service.MovieService;
import com.movie.service.OrderService;
import com.movie.service.ScreeningService;
import com.movie.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/jsp")
@RequiredArgsConstructor
public class JspViewController {

    private final MovieService movieService;
    private final OrderService orderService;
    private final ScreeningService screeningService;
    private final UserService userService;

    @GetMapping("/movies")
    public String movies(Model model) {
        model.addAttribute("movies", movieService.list());
        model.addAttribute("totalMovies", movieService.count());
        model.addAttribute("pageTitle", "电影列表");
        model.addAttribute("minPrice", 20.0);
        model.addAttribute("maxPrice", 100.0);
        model.addAttribute("searchKeyword", "");
        return "admin/movies";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("movies", movieService.list());
        model.addAttribute("totalMovies", movieService.count());
        model.addAttribute("totalUsers", userService.count());
        model.addAttribute("totalOrders", orderService.count());
        model.addAttribute("totalScreenings", screeningService.count());

        model.addAttribute("systemVersion", "v3.2.0");
        model.addAttribute("features", Arrays.asList("在线选座", "会员折扣", "在线支付", "评论评分", "电影推荐"));

        Map<String, Object> stats = new HashMap<>();
        stats.put("avgPrice", 49.90);
        stats.put("totalSales", 1280000);
        stats.put("ratingAvg", 8.5);
        model.addAttribute("stats", stats);

        return "admin/dashboard";
    }
}
