package com.movie.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/api/*")
public class RequestLogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("[过滤器] RequestLogFilter 初始化完成");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        long start = System.currentTimeMillis();

        chain.doFilter(request, response);

        long elapsed = System.currentTimeMillis() - start;

        System.out.println("[过滤器] " + req.getMethod() + " " + req.getRequestURI()
                + (req.getQueryString() != null ? "?" + req.getQueryString() : "")
                + " → " + elapsed + "ms");
    }

    @Override
    public void destroy() {
        System.out.println("[过滤器] RequestLogFilter 已销毁");
    }
}
