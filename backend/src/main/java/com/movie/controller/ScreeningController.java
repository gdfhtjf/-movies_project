package com.movie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.movie.common.Result;
import com.movie.entity.Movie;
import com.movie.entity.Screening;
import com.movie.service.MovieService;
import com.movie.service.OrderService;
import com.movie.service.ScreeningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/screenings")
@RequiredArgsConstructor
public class ScreeningController {

    private final ScreeningService screeningService;
    private final OrderService orderService;
    private final MovieService movieService;

    @GetMapping
    public Result<Page<Screening>> list(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) Integer movieId,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) String hallNumber) {
        if (movieId != null && page == 1 && size == 10 && keyword == null && hallNumber == null) {
            List<Screening> list = screeningService.listByMovieId(movieId);
            Movie movie = movieService.getById(movieId);
            for (Screening s : list) {
                if (s.getPrice() == null && movie != null) {
                    s.setPrice(movie.getPrice());
                }
                if (s.getShowDate() == null && s.getStartTime() != null) {
                    s.setShowDate(s.getStartTime());
                }
                if (s.getRemainingSeats() == null) {
                    s.setRemainingSeats(s.getTotalSeats() != null ? s.getTotalSeats() : 0);
                }
                if (s.getTotalSeats() == null) {
                    s.setTotalSeats(s.getRemainingSeats() != null ? s.getRemainingSeats() : 0);
                }
            }
            log.info("电影[{}] 场次查询: 共 {} 条", movie != null ? movie.getTitle() : movieId, list.size());
            Page<Screening> result = new Page<>(1, list.size());
            result.setRecords(list);
            result.setTotal(list.size());
            return Result.success(result);
        }
        return Result.success(screeningService.pageScreenings(page, size, movieId, keyword, hallNumber));
    }

    @GetMapping("/{id}")
    public Result<Screening> detail(@PathVariable Integer id) {
        Screening screening = screeningService.getById(id);
        if (screening == null) {
            return Result.error(404, "场次不存在");
        }
        return Result.success(screening);
    }

    @PostMapping
    public Result<Screening> add(@Valid @RequestBody Screening screening) {
        if (screening.getStartTime() != null && screening.getStartTime().isBefore(LocalDateTime.now())) {
            return Result.error(400, "开始时间不能是过去时间");
        }
        if (screening.getEndTime() != null && screening.getStartTime() != null
                && screening.getEndTime().isBefore(screening.getStartTime())) {
            return Result.error(400, "结束时间不能早于开始时间");
        }
        // 检查同一影厅时间冲突
        if (screening.getHallNumber() != null && screening.getStartTime() != null && screening.getEndTime() != null) {
            if (screeningService.hasTimeConflict(screening.getHallNumber(), screening.getStartTime(), screening.getEndTime(), null)) {
                return Result.error(409, "该影厅在该时间段已有场次安排");
            }
        }
        if (screening.getStatus() == null || screening.getStatus().isEmpty()) {
            screening.setStatus("AVAILABLE");
        }
        if (screening.getPrice() == null) {
            screening.setPrice(java.math.BigDecimal.ZERO);
        }
        if (screening.getShowDate() == null && screening.getStartTime() != null) {
            screening.setShowDate(screening.getStartTime());
        }
        screening.setRemainingSeats(screening.getTotalSeats());
        screening.setVersion(1);
        screeningService.save(screening);
        return Result.success("添加成功", screening);
    }

    @PutMapping("/{id}")
    public Result<Screening> update(@PathVariable Integer id, @RequestBody Screening screening) {
        Screening existing = screeningService.getById(id);
        if (existing == null) {
            return Result.error(404, "场次不存在");
        }

        // 检查同一影厅时间冲突（使用请求中的值，若未提供则沿用现有值）
        String effectiveHallNumber = screening.getHallNumber() != null ? screening.getHallNumber() : existing.getHallNumber();
        LocalDateTime effectiveStartTime = screening.getStartTime() != null ? screening.getStartTime() : existing.getStartTime();
        LocalDateTime effectiveEndTime = screening.getEndTime() != null ? screening.getEndTime() : existing.getEndTime();
        if (effectiveHallNumber != null && effectiveStartTime != null && effectiveEndTime != null) {
            if (screeningService.hasTimeConflict(effectiveHallNumber, effectiveStartTime, effectiveEndTime, id)) {
                return Result.error(409, "该影厅在该时间段已有场次安排");
            }
        }

        if (screening.getTotalSeats() != null) {
            int soldSeats = existing.getTotalSeats() - existing.getRemainingSeats();
            if (screening.getTotalSeats() < soldSeats) {
                return Result.error(400, "总座位数不能小于已售座位数(当前已售: " + soldSeats + ")");
            }
            int newRemaining = screening.getTotalSeats() - soldSeats;
            screening.setRemainingSeats(newRemaining);
        } else {
            screening.setRemainingSeats(existing.getRemainingSeats());
        }

        screening.setId(id);
        screeningService.updateById(screening);
        return Result.success("更新成功", screening);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        screeningService.deleteScreeningWithCheck(id);
        return Result.success("删除成功", null);
    }
}
