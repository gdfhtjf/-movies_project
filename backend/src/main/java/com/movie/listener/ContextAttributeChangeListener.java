package com.movie.listener;

import jakarta.servlet.ServletContextAttributeEvent;
import jakarta.servlet.ServletContextAttributeListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ContextAttributeChangeListener implements ServletContextAttributeListener {

    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        System.out.println("[监听器-ContextAttr] 添加全局属性: " + event.getName()
                + " = " + event.getValue());
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
        System.out.println("[监听器-ContextAttr] 替换全局属性: " + event.getName()
                + " → " + event.getValue() + " (旧值: "
                + event.getServletContext().getAttribute(event.getName()) + ")");
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
        System.out.println("[监听器-ContextAttr] 移除全局属性: " + event.getName()
                + " = " + event.getValue());
    }
}
