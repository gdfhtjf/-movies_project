package com.movie.config;

import com.movie.interceptor.AdminInterceptor;
import com.movie.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/orders/**")
                .addPathPatterns("/api/auth/logout")
                .addPathPatterns("/api/users/*/password");

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/movies/**")
                .addPathPatterns("/api/screenings/**")
                .addPathPatterns("/api/posters/**")
                .addPathPatterns("/api/orders/admin/**")
                .addPathPatterns("/api/users/**")
                .addPathPatterns("/api/admin/**")
                .addPathPatterns("/api/operation-logs/**")
                .addPathPatterns("/api/payment/**")
                .addPathPatterns("/api/hall-layouts/**")
                .addPathPatterns("/api/genres/**")
                .excludePathPatterns("/api/users/*/password");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
