package com.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("hall_layouts")
public class HallLayout {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("hall_number")
    private String hallNumber;

    @TableField("rows_num")
    private Integer rowsNum;

    @TableField("cols_num")
    private Integer colsNum;

    @TableField("layout_json")
    private String layoutJson;

    @TableField(exist = false)
    private LocalDateTime createTime;
}
