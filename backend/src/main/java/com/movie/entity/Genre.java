package com.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("genres")
public class Genre {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    @TableField(exist = false)
    private LocalDateTime createTime;
}
