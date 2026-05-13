package com.movie.listener;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpServletRequest;

@WebListener
public class RequestTimeListener implements ServletRequestListener {

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        sre.getServletRequest().setAttribute("requestStartTime", System.currentTimeMillis());
        if (sre.getServletRequest() instanceof HttpServletRequest req) {
            System.out.println("[监听器-RequestTime] 请求开始: " + req.getMethod() + " " + req.getRequestURI());
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        Long start = (Long) sre.getServletRequest().getAttribute("requestStartTime");
        if (start != null) {
            long elapsed = System.currentTimeMillis() - start;
            sre.getServletRequest().setAttribute("requestElapsed", elapsed);
            System.out.println("[监听器-RequestTime] 请求结束，耗时: " + elapsed + "ms");
        }
    }
}
