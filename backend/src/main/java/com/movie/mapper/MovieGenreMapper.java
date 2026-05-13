package com.movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.movie.entity.MovieGenre;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MovieGenreMapper extends BaseMapper<MovieGenre> {
}
