package com.movie.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SpaFallbackFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("[过滤器-SpaFallback] 初始化完成");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();

        if (!path.startsWith("/api/")
                && !path.startsWith("/uploads/")
                && !path.startsWith("/jsp/")
                && !path.equals("/")
                && !path.contains(".")
                && !path.startsWith("/admin/")) {
            req.getRequestDispatcher("/index.html").forward(request, response);
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("[过滤器-SpaFallback] 已销毁");
    }
}
