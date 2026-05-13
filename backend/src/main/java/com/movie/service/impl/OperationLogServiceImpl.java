package com.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.entity.OperationLog;
import com.movie.mapper.OperationLogMapper;
import com.movie.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public Page<OperationLog> pageLogs(Page<OperationLog> page, String keyword) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(OperationLog::getUsername, keyword)
                    .or()
                    .like(OperationLog::getUri, keyword));
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);
        return this.page(page, wrapper);
    }
}
