package com.movie.util;

import com.movie.entity.Movie;
import com.movie.entity.Screening;
import com.movie.mapper.MovieMapper;
import com.movie.mapper.ScreeningMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class ScreeningDataGenerator implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ScreeningMapper screeningMapper;

    @Autowired
    private MovieMapper movieMapper;

    private static final boolean SHOULD_GENERATE = false;
    private static final int DAYS_TO_GENERATE = 7;

    private final Random random = new Random();
    private final String[] halls = { "1号厅", "2号厅", "3号厅", "4号厅", "5号厅", "IMAX厅", "VIP厅" };
    private final int[] seats = { 80, 100, 120, 90, 60, 200, 30 };
    private final LocalTime[] timeSlots = {
        LocalTime.of(9, 30),
        LocalTime.of(13, 0),
        LocalTime.of(16, 0),
        LocalTime.of(19, 0),
        LocalTime.of(21, 30),
    };

    @Override
    public void run(String... args) {
        if (!SHOULD_GENERATE) {
            System.out.println("[ScreeningGenerator] 跳过场次数据生成");
            return;
        }

        System.out.println("[ScreeningGenerator] 开始迁移表结构...");
        migrateTable();

        System.out.println("[ScreeningGenerator] 开始生成场次数据...");

        List<Movie> movies = movieMapper.selectList(null);
        if (movies.isEmpty()) {
            System.out.println("[ScreeningGenerator] 没有电影数据，跳过场次生成");
            return;
        }

        long existingCount = screeningMapper.selectCount(null);
        if (existingCount > 0) {
            System.out.println("[ScreeningGenerator] 清空旧场次数据(" + existingCount + "条)...");
            try {
                screeningMapper.delete(null);
            } catch (Exception e) {
                System.out.println("[ScreeningGenerator] 清空失败(外键约束): " + e.getMessage() + ", 直接添加新数据");
            }
        }

        LocalDate today = LocalDate.now();
        int totalGenerated = 0;

        for (int dayOffset = 0; dayOffset < DAYS_TO_GENERATE; dayOffset++) {
            LocalDate screeningDate = today.plusDays(dayOffset);
            int moviesPerDay = Math.min(movies.size(), 5 + random.nextInt(3));

            for (int i = 0; i < moviesPerDay && i < movies.size(); i++) {
                Movie movie = movies.get(i);
                int hallIdx = random.nextInt(halls.length);
                int timeSlotIdx = random.nextInt(timeSlots.length);
                int duration = movie.getDuration() != null ? movie.getDuration() : 120;

                LocalDateTime startTime = LocalDateTime.of(screeningDate, timeSlots[timeSlotIdx]);
                LocalDateTime endTime = startTime.plusMinutes(duration + 30);

                BigDecimal price = movie.getPrice() != null ? movie.getPrice()
                        : new BigDecimal("30.00");

                Screening s = new Screening();
                s.setMovieId(movie.getId());
                s.setHallNumber(halls[hallIdx]);
                s.setStartTime(startTime);
                s.setEndTime(endTime);
                s.setPrice(price);
                s.setShowDate(startTime);
                s.setTotalSeats(seats[hallIdx]);
                s.setRemainingSeats(seats[hallIdx]);
                s.setStatus("AVAILABLE");
                s.setVersion(1);

                screeningMapper.insert(s);
                totalGenerated++;
            }
        }

        System.out.println("[ScreeningGenerator] 场次数据生成完成！共生成 " + totalGenerated + " 条场次记录");
    }

    private void migrateTable() {
        try {
            jdbcTemplate.execute("ALTER TABLE screenings MODIFY COLUMN show_time DATETIME NULL");
            System.out.println("[ScreeningGenerator] 已将 show_time 设为可为空");
        } catch (Exception e) {
            System.out.println("[ScreeningGenerator] show_time 修改失败: " + e.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE screenings CHANGE COLUMN show_time start_time DATETIME NULL");
            System.out.println("[ScreeningGenerator] 已重命名 show_time -> start_time");
        } catch (Exception e) {
            System.out.println("[ScreeningGenerator] 重命名跳过: " + e.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE screenings ADD COLUMN end_time DATETIME NULL");
        } catch (Exception e) {
            System.out.println("[ScreeningGenerator] end_time 已存在或添加失败");
        }

        try {
            jdbcTemplate.execute("ALTER TABLE screenings ADD COLUMN price DECIMAL(10,2) NULL");
        } catch (Exception e) {
            System.out.println("[ScreeningGenerator] price 已存在或添加失败");
        }

        try {
            jdbcTemplate.execute("ALTER TABLE screenings ADD COLUMN status VARCHAR(20) NULL DEFAULT 'AVAILABLE'");
        } catch (Exception e) {
            System.out.println("[ScreeningGenerator] status 已存在或添加失败");
        }

        try {
            jdbcTemplate.execute("ALTER TABLE screenings ADD COLUMN show_date DATETIME NULL");
        } catch (Exception e) {
            System.out.println("[ScreeningGenerator] show_date 已存在或添加失败");
        }

        System.out.println("[ScreeningGenerator] 表结构迁移完成");
    }
}