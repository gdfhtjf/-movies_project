package com.movie;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.movie.entity.Movie;
import com.movie.entity.Screening;
import com.movie.entity.User;
import com.movie.service.MovieService;
import com.movie.service.OrderService;
import com.movie.service.ScreeningService;
import com.movie.service.UserService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Javaweb0001ApplicationTests {

    @Autowired private MovieService movieService;
    @Autowired private ScreeningService screeningService;
    @Autowired private OrderService orderService;
    @Autowired private UserService userService;

    private static Integer testMovieId;
    private static Integer testScreeningId;
    private static Integer testUserId;
    private static Integer testOrderId;

    // ==================== Users 模块 ====================

    @Test
    @Order(1)
    void testUserRegister() {
        User user = userService.register("testuser_" + System.currentTimeMillis(), "123456");
        assertNotNull(user);
        assertEquals("user", user.getRole());
        testUserId = user.getId();
    }

    @Test
    @Order(2)
    void testUserRegisterDuplicate() {
        assertThrows(RuntimeException.class, () -> userService.register("Admin", "123456"));
    }

    @Test
    @Order(3)
    void testUserLogin() {
        User user = userService.login("Admin", "123456");
        assertNotNull(user);
        assertEquals("admin", user.getRole());
    }

    @Test
    @Order(4)
    void testUserLoginWrongPassword() {
        assertThrows(RuntimeException.class, () -> userService.login("Admin", "wrongpwd"));
    }

    @Test
    @Order(5)
    void testChangePassword() {
        userService.changePassword(testUserId, "123456", "654321");
        assertThrows(RuntimeException.class, () -> userService.changePassword(testUserId, "123456", "newpwd"));
    }

    @Test
    @Order(6)
    void testPageUsers() {
        Page<User> page = userService.pageUsers(1, 10, null);
        assertNotNull(page);
        assertTrue(page.getTotal() > 0);
    }

    // ==================== Movies 模块 ====================

    @Test
    @Order(10)
    void testMovieAdd() {
        Movie movie = new Movie();
        movie.setTitle("测试电影_" + System.currentTimeMillis());
        movie.setDirector("测试导演");
        movie.setPrice(new BigDecimal("39.90"));
        movie.setStock(100);
        movie.setDescription("这是一部测试电影");
        movie.setCast("测试演员A,测试演员B");
        movie.setGenre("测试,科幻");
        movie.setDuration(120);
        movie.setCreateTime(LocalDateTime.now());
        movie.setUpdateTime(LocalDateTime.now());
        movieService.save(movie);
        testMovieId = movie.getId();
        assertNotNull(testMovieId);
    }

    @Test
    @Order(11)
    void testMovieDetail() {
        Movie movie = movieService.getById(testMovieId);
        assertNotNull(movie);
        assertEquals("测试导演", movie.getDirector());
    }

    @Test
    @Order(12)
    void testMovieDetailNotFound() {
        Movie movie = movieService.getById(99999);
        assertTrue(movie == null);
    }

    @Test
    @Order(13)
    void testMovieListWithPagination() {
        Page<Movie> page = movieService.pageMovies(1, 5, null, null, null, null, null, null);
        assertNotNull(page);
        assertTrue(page.getRecords().size() <= 5);
    }

    @Test
    @Order(14)
    void testMovieListWithKeyword() {
        Page<Movie> page = movieService.pageMovies(1, 10, "测试", null, null, null, null, null);
        assertNotNull(page);
        assertTrue(page.getTotal() > 0);
    }

    @Test
    @Order(15)
    void testMovieListWithGenreFilter() {
        Page<Movie> page = movieService.pageMovies(1, 10, null, "科幻", null, null, null, null);
        assertNotNull(page);
    }

    @Test
    @Order(16)
    void testMovieListWithPriceRange() {
        Page<Movie> page = movieService.pageMovies(1, 10, null, null, new BigDecimal("30"), new BigDecimal("100"), null, null);
        assertNotNull(page);
    }

    @Test
    @Order(17)
    void testMovieUpdate() {
        Movie movie = movieService.getById(testMovieId);
        movie.setDirector("更新后的导演");
        movie.setUpdateTime(LocalDateTime.now());
        movieService.updateById(movie);
        Movie updated = movieService.getById(testMovieId);
        assertEquals("更新后的导演", updated.getDirector());
    }

    // ==================== Screenings 模块 ====================

    @Test
    @Order(20)
    void testScreeningAdd() {
        Screening screening = new Screening();
        screening.setMovieId(testMovieId);
        screening.setHallNumber("测试影厅1");
        screening.setStartTime(LocalDateTime.now().plusDays(3));
        screening.setEndTime(LocalDateTime.now().plusDays(3).plusHours(2));
        screening.setPrice(new java.math.BigDecimal("35.00"));
        screening.setStatus("AVAILABLE");
        screening.setShowDate(LocalDateTime.now().plusDays(3));
        screening.setTotalSeats(100);
        screening.setRemainingSeats(100);
        screening.setVersion(1);
        screeningService.save(screening);
        testScreeningId = screening.getId();
        assertNotNull(testScreeningId);
    }

    @Test
    @Order(21)
    void testScreeningDetail() {
        Screening screening = screeningService.getById(testScreeningId);
        assertNotNull(screening);
        assertEquals("测试影厅1", screening.getHallNumber());
    }

    @Test
    @Order(22)
    void testScreeningPage() {
        Page<Screening> page = screeningService.pageScreenings(1, 10, null, null, null);
        assertNotNull(page);
    }

    @Test
    @Order(23)
    void testScreeningListByMovieId() {
        List<Screening> list = screeningService.listByMovieId(testMovieId);
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }

    @Test
    @Order(24)
    void testScreeningUpdate() {
        Screening screening = screeningService.getById(testScreeningId);
        screening.setHallNumber("更新影厅");
        screeningService.updateById(screening);
        Screening updated = screeningService.getById(testScreeningId);
        assertEquals("更新影厅", updated.getHallNumber());
    }

    // ==================== Orders 模块 ====================

    @Test
    @Order(30)
    void testCreateOrder() {
        com.movie.entity.Order order = orderService.createOrder(testUserId, testScreeningId, Arrays.asList("A1", "A2"));
        assertNotNull(order);
        assertEquals("paid", order.getStatus());
        assertEquals(new BigDecimal("79.80"), order.getTotalPrice());
        testOrderId = order.getId();
    }

    @Test
    @Order(31)
    void testCreateOrderSeatOccupied() {
        assertThrows(RuntimeException.class,
                () -> orderService.createOrder(testUserId, testScreeningId, Arrays.asList("A1")));
    }

    @Test
    @Order(32)
    void testCreateOrderInsufficientSeats() {
        Screening s = screeningService.getById(testScreeningId);
        int remaining = s.getRemainingSeats();
        assertThrows(RuntimeException.class,
                () -> orderService.createOrder(testUserId, testScreeningId,
                        java.util.Collections.nCopies(remaining + 10, "X1")));
    }

    @Test
    @Order(33)
    void testMyOrders() {
        Page<com.movie.entity.Order> page = orderService.pageMyOrders(testUserId, 1, 10);
        assertNotNull(page);
        assertTrue(page.getTotal() > 0);
    }

    @Test
    @Order(34)
    void testAdminOrders() {
        Page<com.movie.entity.Order> page = orderService.pageAdminOrders(1, 10, null, null);
        assertNotNull(page);
    }

    @Test
    @Order(35)
    void testAdminOrdersByStatus() {
        Page<com.movie.entity.Order> page = orderService.pageAdminOrders(1, 10, null, "paid");
        assertNotNull(page);
        assertTrue(page.getTotal() > 0);
    }

    @Test
    @Order(36)
    void testCancelOrder() {
        orderService.cancelOrder(testOrderId, testUserId);
        com.movie.entity.Order cancelled = orderService.getById(testOrderId);
        assertEquals("cancelled", cancelled.getStatus());
        assertNotNull(cancelled.getCancelTime());
    }

    @Test
    @Order(37)
    void testCancelOrderTwice() {
        assertThrows(RuntimeException.class, () -> orderService.cancelOrder(testOrderId, testUserId));
    }

    @Test
    @Order(38)
    void testCancelOrderWrongUser() {
        com.movie.entity.Order newOrder = orderService.createOrder(testUserId, testScreeningId, Arrays.asList("B1"));
        assertThrows(RuntimeException.class, () -> orderService.cancelOrder(newOrder.getId(), 99999));
        orderService.cancelOrder(newOrder.getId(), testUserId);
    }

    // ==================== 级联删除检查 ====================

    @Test
    @Order(40)
    void testScreeningDeleteWithoutOrders() {
        Screening s = new Screening();
        s.setMovieId(testMovieId);
        s.setHallNumber("临时影厅");
        s.setStartTime(LocalDateTime.now().plusDays(10));
        s.setEndTime(LocalDateTime.now().plusDays(10).plusHours(2));
        s.setPrice(new java.math.BigDecimal("35.00"));
        s.setStatus("AVAILABLE");
        s.setShowDate(LocalDateTime.now().plusDays(10));
        s.setTotalSeats(50);
        s.setRemainingSeats(50);
        s.setVersion(1);
        screeningService.save(s);
        screeningService.deleteScreeningWithCheck(s.getId());
        assertTrue(screeningService.getById(s.getId()) == null);
    }

    @Test
    @Order(41)
    void testScreeningDeleteWithOrders() {
        // 测试级联删除：删除场次时会删除该场次的所有订单
        long beforeCount = orderService.lambdaQuery()
                .eq(com.movie.entity.Order::getScreeningId, testScreeningId)
                .count();
        assertTrue(beforeCount > 0);
        screeningService.deleteScreeningWithCheck(testScreeningId);
        assertTrue(screeningService.getById(testScreeningId) == null);
        long afterCount = orderService.lambdaQuery()
                .eq(com.movie.entity.Order::getScreeningId, testScreeningId)
                .count();
        assertEquals(0, afterCount);
    }

    @Test
    @Order(42)
    void testMovieDeleteWithoutOrders() {
        Movie m = new Movie();
        m.setTitle("临时电影");
        m.setPrice(new BigDecimal("29.90"));
        m.setCreateTime(LocalDateTime.now());
        m.setUpdateTime(LocalDateTime.now());
        movieService.save(m);
        movieService.deleteMovieWithCheck(m.getId());
        assertTrue(movieService.getById(m.getId()) == null);
    }

    @Test
    @Order(43)
    void testMovieDeleteWithOrders() {
        // 测试级联删除：删除电影时会删除该电影的所有场次和订单
        movieService.deleteMovieWithCheck(testMovieId);
        assertTrue(movieService.getById(testMovieId) == null);
        // 确认场次也被删除
        long screeningCount = screeningService.lambdaQuery()
                .eq(Screening::getMovieId, testMovieId)
                .count();
        assertEquals(0, screeningCount);
    }
}
