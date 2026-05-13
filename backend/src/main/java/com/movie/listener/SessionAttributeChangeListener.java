package com.movie.listener;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;

@WebListener
public class SessionAttributeChangeListener implements HttpSessionAttributeListener {

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        System.out.println("[监听器-SessionAttr] 添加属性: " + event.getName()
                + " = " + event.getValue() + " [会话ID: " + event.getSession().getId() + "]");
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        Object oldValue = event.getSession().getAttribute(event.getName());
        System.out.println("[监听器-SessionAttr] 替换属性: " + event.getName()
                + " → " + event.getValue() + " (旧值: " + oldValue + ")");
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        System.out.println("[监听器-SessionAttr] 移除属性: " + event.getName()
                + " = " + event.getValue() + " [会话ID: " + event.getSession().getId() + "]");
    }
}
