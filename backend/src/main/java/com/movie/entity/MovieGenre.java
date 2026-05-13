package com.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("movie_genre")
public class MovieGenre {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("movie_id")
    private Integer movieId;

    @TableField("genre_id")
    private Integer genreId;
}
