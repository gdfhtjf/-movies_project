package com.movie.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@WebFilter(urlPatterns = "/api/*")
public class RateLimitFilter implements Filter {

    private static final int MAX_REQUESTS_PER_MINUTE = 10000;
    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> windowStart = new ConcurrentHashMap<>();

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("[过滤器-RateLimit] 初始化完成，限制: " + MAX_REQUESTS_PER_MINUTE + "次/分钟");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String clientIp = getClientIp(req);
        long now = System.currentTimeMillis();
        long windowKey = now / 60000;

        String rateKey = clientIp + "_" + windowKey;
        windowStart.putIfAbsent(rateKey, windowKey);
        requestCounts.putIfAbsent(rateKey, new AtomicInteger(0));

        int count = requestCounts.get(rateKey).incrementAndGet();

        if (count > MAX_REQUESTS_PER_MINUTE) {
            System.out.println("[过滤器-RateLimit] IP " + clientIp + " 请求过于频繁，已拦截");
            resp.setStatus(429);
            resp.getWriter().write("{\"code\":429,\"message\":\"请求过于频繁，请稍后再试\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    public void destroy() {
        System.out.println("[过滤器-RateLimit] 已销毁");
    }
}
