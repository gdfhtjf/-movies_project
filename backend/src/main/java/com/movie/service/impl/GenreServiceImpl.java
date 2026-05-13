package com.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.entity.Genre;
import com.movie.entity.MovieGenre;
import com.movie.mapper.GenreMapper;
import com.movie.mapper.MovieGenreMapper;
import com.movie.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl extends ServiceImpl<GenreMapper, Genre> implements GenreService {

    private final MovieGenreMapper movieGenreMapper;

    @Override
    public List<Genre> getGenresByMovieId(Integer movieId) {
        List<Integer> genreIds = movieGenreMapper.selectList(
                new LambdaQueryWrapper<MovieGenre>().eq(MovieGenre::getMovieId, movieId))
                .stream().map(MovieGenre::getGenreId).toList();
        if (genreIds.isEmpty()) {
            return List.of();
        }
        return lambdaQuery().in(Genre::getId, genreIds).list();
    }

    @Override
    @Transactional
    public void setMovieGenres(Integer movieId, List<Integer> genreIds) {
        movieGenreMapper.delete(new LambdaQueryWrapper<MovieGenre>()
                .eq(MovieGenre::getMovieId, movieId));
        for (Integer genreId : genreIds) {
            MovieGenre mg = new MovieGenre();
            mg.setMovieId(movieId);
            mg.setGenreId(genreId);
            movieGenreMapper.insert(mg);
        }
    }
}
