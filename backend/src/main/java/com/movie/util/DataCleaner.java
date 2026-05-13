package com.movie.util;

import com.movie.mapper.MovieMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataCleaner implements CommandLineRunner {

    @Autowired
    private MovieMapper movieMapper;

    private static final boolean CLEAN_DUPLICATES = false;

    @Override
    public void run(String... args) {
        if (!CLEAN_DUPLICATES) {
            System.out.println("[DataCleaner] 跳过清理重复数据");
            return;
        }

        System.out.println("[DataCleaner] 开始清理重复电影数据...");
        
        try {
            List<com.movie.entity.Movie> allMovies = movieMapper.selectList(null);
            System.out.println("[DataCleaner] 当前电影总数: " + allMovies.size());

            Map<String, List<Integer>> titleToIds = new HashMap<>();
            for (com.movie.entity.Movie movie : allMovies) {
                titleToIds.computeIfAbsent(movie.getTitle(), k -> new ArrayList<>()).add(movie.getId());
            }

            int deleted = 0;
            for (Map.Entry<String, List<Integer>> entry : titleToIds.entrySet()) {
                List<Integer> ids = entry.getValue();
                if (ids.size() > 1) {
                    System.out.println("[DataCleaner] 发现重复: " + entry.getKey() + " 有 " + ids.size() + " 条记录");
                    for (int i = 1; i < ids.size(); i++) {
                        movieMapper.deleteById(ids.get(i));
                        deleted++;
                    }
                }
            }

            System.out.println("[DataCleaner] 清理完成！删除重复记录: " + deleted + " 条");
        } catch (Exception e) {
            System.err.println("[DataCleaner] 清理失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}