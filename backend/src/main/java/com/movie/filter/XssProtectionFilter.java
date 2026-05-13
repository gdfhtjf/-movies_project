package com.movie.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

@WebFilter(urlPatterns = "/api/*")
public class XssProtectionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("[过滤器-XssProtection] 初始化完成");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(req) {
            @Override
            public String getParameter(String name) {
                String value = super.getParameter(name);
                return escapeXss(value);
            }

            @Override
            public String[] getParameterValues(String name) {
                String[] values = super.getParameterValues(name);
                if (values != null) {
                    String[] escaped = new String[values.length];
                    for (int i = 0; i < values.length; i++) {
                        escaped[i] = escapeXss(values[i]);
                    }
                    return escaped;
                }
                return null;
            }
        };

        chain.doFilter(wrapper, response);
    }

    private String escapeXss(String value) {
        if (value == null) return null;
        String escaped = value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
        if (!escaped.equals(value)) {
            System.out.println("[过滤器-XssProtection] 已转义XSS内容");
        }
        return escaped;
    }

    @Override
    public void destroy() {
        System.out.println("[过滤器-XssProtection] 已销毁");
    }
}
