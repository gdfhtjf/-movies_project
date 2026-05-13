package com.movie;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@SpringBootApplication
@MapperScan("com.movie.mapper")
@EnableJdbcHttpSession
public class Javaweb0001Application {

    public static void main(String[] args) {
        SpringApplication.run(Javaweb0001Application.class, args);
    }

}
