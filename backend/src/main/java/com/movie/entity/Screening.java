package com.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("screenings")
public class Screening {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @NotNull(message = "关联电影ID不能为空")
    @TableField("movie_id")
    private Integer movieId;

    @NotBlank(message = "影厅号不能为空")
    @Size(min = 1, max = 20, message = "影厅号长度必须在1到20之间")
    @TableField("hall_number")
    private String hallNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "开始时间不能为空")
    @TableField("start_time")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "结束时间不能为空")
    @TableField("end_time")
    private LocalDateTime endTime;

    @NotNull(message = "票价不能为空")
    @Min(value = 0, message = "票价不能为负数")
    @TableField("price")
    private BigDecimal price;

    @NotBlank(message = "场次状态不能为空")
    @Size(max = 20, message = "状态长度不能超过20")
    @TableField("status")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("show_date")
    private LocalDateTime showDate;

    @NotNull(message = "总座位数不能为空")
    @Min(value = 1, message = "总座位数至少为1")
    @TableField("total_seats")
    private Integer totalSeats;

    @Min(value = 0, message = "剩余座位数不能为负数")
    @TableField("remaining_seats")
    private Integer remainingSeats;

    private Integer version;

    @TableField(exist = false)
    private String movieTitle;
}
