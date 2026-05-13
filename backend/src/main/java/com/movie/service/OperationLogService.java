package com.movie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.entity.OperationLog;

public interface OperationLogService extends IService<OperationLog> {

    Page<OperationLog> pageLogs(Page<OperationLog> page, String keyword);
}
