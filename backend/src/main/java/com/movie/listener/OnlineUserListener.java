package com.movie.listener;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;

@WebListener
public class OnlineUserListener implements HttpSessionListener {

    private static final AtomicInteger onlineCount = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        int count = onlineCount.incrementAndGet();
        se.getSession().getServletContext().setAttribute("onlineCount", count);
        System.out.println("[监听器-OnlineUser] 新会话创建，当前在线: " + count);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        int count = onlineCount.decrementAndGet();
        se.getSession().getServletContext().setAttribute("onlineCount", count);
        System.out.println("[监听器-OnlineUser] 会话销毁，当前在线: " + count);
    }

    public static int getOnlineCount() {
        return onlineCount.get();
    }
}
