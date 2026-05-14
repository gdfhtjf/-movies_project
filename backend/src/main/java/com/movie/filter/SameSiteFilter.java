package com.movie.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class SameSiteFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        SameSiteResponseWrapper wrapped = new SameSiteResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, wrapped);
    }

    @Override
    public void destroy() {
    }

    private static class SameSiteResponseWrapper extends HttpServletResponseWrapper {
        SameSiteResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setHeader(String name, String value) {
            if ("Set-Cookie".equalsIgnoreCase(name) && value != null) {
                value = value.replace("SameSite=Lax", "SameSite=None");
            }
            super.setHeader(name, value);
        }

        @Override
        public void addHeader(String name, String value) {
            if ("Set-Cookie".equalsIgnoreCase(name) && value != null) {
                value = value.replace("SameSite=Lax", "SameSite=None");
            }
            super.addHeader(name, value);
        }
    }
}
