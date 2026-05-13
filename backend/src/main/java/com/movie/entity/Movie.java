package com.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("movies")
public class Movie {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "电影标题不能为空")
    @Size(min = 1, max = 100, message = "标题长度必须在1到100之间")
    private String title;

    @Size(max = 50, message = "导演名称不能超过50个字符")
    private String director;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    @Size(max = 255, message = "海报路径不能超过255个字符")
    @TableField("poster_path")
    private String posterPath;

    @Size(max = 255, message = "预告片路径不能超过255个字符")
    @TableField("trailer_path")
    private String trailerPath;

    private String description;

    private String cast;

    @TableField("release_date")
    private LocalDate releaseDate;

    private Integer duration;

    @Size(max = 100, message = "类型标签不能超过100个字符")
    private String genre;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<String> genreNames;
}
