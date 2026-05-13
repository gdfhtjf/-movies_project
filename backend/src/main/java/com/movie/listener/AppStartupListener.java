package com.movie.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.time.LocalDateTime;

@WebListener
public class AppStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("appStartTime", LocalDateTime.now().toString());
        sce.getServletContext().setAttribute("appName", "在线电影购票系统");
        System.out.println("========== [监听器] " + sce.getServletContext().getAttribute("appName")
                + " 启动完成，时间: " + sce.getServletContext().getAttribute("appStartTime") + " ==========");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("========== [监听器] " + sce.getServletContext().getAttribute("appName")
                + " 正在关闭... ==========");
    }
}
