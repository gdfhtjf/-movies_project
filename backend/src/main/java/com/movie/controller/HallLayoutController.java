package com.movie.controller;

import com.movie.common.Result;
import com.movie.entity.HallLayout;
import com.movie.service.HallLayoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hall-layouts")
@RequiredArgsConstructor
public class HallLayoutController {

    private final HallLayoutService hallLayoutService;

    @GetMapping("/by-hall")
    public Result<HallLayout> getByHallNumber(@RequestParam String hallNumber) {
        HallLayout layout = hallLayoutService.getLayoutByHallNumber(hallNumber);
        if (layout == null) {
            return Result.error(404, "影厅布局未找到");
        }
        return Result.success(layout);
    }
}
