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
import java.util.Arrays;
import java.util.List;

@WebFilter(urlPatterns = "/api/*")
public class SensitiveWordFilter implements Filter {

    private static final List<String> SENSITIVE_WORDS = Arrays.asList(
            "fuck", "shit", "damn", "ass", "bitch"
    );

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("[过滤器-SensitiveWord] 初始化完成，敏感词数量: " + SENSITIVE_WORDS.size());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String method = req.getMethod();

        if ("GET".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(req) {
            @Override
            public String getParameter(String name) {
                String value = super.getParameter(name);
                return filterSensitive(value);
            }

            @Override
            public String[] getParameterValues(String name) {
                String[] values = super.getParameterValues(name);
                if (values != null) {
                    String[] filtered = new String[values.length];
                    for (int i = 0; i < values.length; i++) {
                        filtered[i] = filterSensitive(values[i]);
                    }
                    return filtered;
                }
                return null;
            }
        };

        chain.doFilter(wrapper, response);
    }

    private String filterSensitive(String value) {
        if (value == null) return null;
        String result = value;
        for (String word : SENSITIVE_WORDS) {
            if (result.toLowerCase().contains(word)) {
                String replacement = "*".repeat(word.length());
                result = result.replaceAll("(?i)" + word, replacement);
                System.out.println("[过滤器-SensitiveWord] 检测到敏感词: " + word + "，已替换");
            }
        }
        return result;
    }

    @Override
    public void destroy() {
        System.out.println("[过滤器-SensitiveWord] 已销毁");
    }
}
