package com.movie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.movie.common.Result;
import com.movie.entity.OperationLog;
import com.movie.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operation-logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    @GetMapping
    public Result<Page<OperationLog>> listLogs(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "20") int size,
                                               @RequestParam(required = false) String keyword) {
        Page<OperationLog> result = operationLogService.pageLogs(new Page<>(page, size), keyword);
        return Result.success(result);
    }
}
