package com.movie.config;

import com.movie.filter.CharEncodingFilter;
import com.movie.filter.CorsFilter;
import com.movie.filter.LoginCheckFilter;
import com.movie.filter.RateLimitFilter;
import com.movie.filter.RequestLogFilter;
import com.movie.filter.SensitiveWordFilter;
import com.movie.filter.XssProtectionFilter;
import com.movie.listener.AppStartupListener;
import com.movie.listener.ContextAttributeChangeListener;
import com.movie.listener.OnlineUserListener;
import com.movie.listener.RequestTimeListener;
import com.movie.listener.SessionAttributeChangeListener;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletComponentConfig {

    @Bean
    public ServletListenerRegistrationBean<AppStartupListener> appStartupListener() {
        return new ServletListenerRegistrationBean<>(new AppStartupListener());
    }

    @Bean
    public ServletListenerRegistrationBean<OnlineUserListener> onlineUserListener() {
        return new ServletListenerRegistrationBean<>(new OnlineUserListener());
    }

    @Bean
    public ServletListenerRegistrationBean<RequestTimeListener> requestTimeListener() {
        return new ServletListenerRegistrationBean<>(new RequestTimeListener());
    }

    @Bean
    public ServletListenerRegistrationBean<SessionAttributeChangeListener> sessionAttributeChangeListener() {
        return new ServletListenerRegistrationBean<>(new SessionAttributeChangeListener());
    }

    @Bean
    public ServletListenerRegistrationBean<ContextAttributeChangeListener> contextAttributeChangeListener() {
        return new ServletListenerRegistrationBean<>(new ContextAttributeChangeListener());
    }

    @Bean
    public FilterRegistrationBean<RequestLogFilter> requestLogFilter() {
        FilterRegistrationBean<RequestLogFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new RequestLogFilter());
        reg.addUrlPatterns("/api/*");
        reg.setOrder(1);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<CharEncodingFilter> charEncodingFilter() {
        FilterRegistrationBean<CharEncodingFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new CharEncodingFilter());
        reg.addUrlPatterns("/*");
        reg.setOrder(2);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new CorsFilter());
        reg.addUrlPatterns("/*");
        reg.setOrder(3);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<LoginCheckFilter> loginCheckFilter() {
        FilterRegistrationBean<LoginCheckFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new LoginCheckFilter());
        reg.addUrlPatterns("/jsp/*", "/admin/*");
        reg.setOrder(4);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<SensitiveWordFilter> sensitiveWordFilter() {
        FilterRegistrationBean<SensitiveWordFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new SensitiveWordFilter());
        reg.addUrlPatterns("/api/*");
        reg.setOrder(5);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<XssProtectionFilter> xssProtectionFilter() {
        FilterRegistrationBean<XssProtectionFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new XssProtectionFilter());
        reg.addUrlPatterns("/api/*");
        reg.setOrder(6);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilter() {
        FilterRegistrationBean<RateLimitFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new RateLimitFilter());
        reg.addUrlPatterns("/api/*");
        reg.setOrder(7);
        return reg;
    }
}
