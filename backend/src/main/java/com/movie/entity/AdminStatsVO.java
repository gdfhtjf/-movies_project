package com.movie.entity;

import lombok.Data;

@Data
public class AdminStatsVO {
    private long movieCount;
    private long screeningCount;
    private long orderCount;
    private long userCount;
    private long paidOrderCount;
}
