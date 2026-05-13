package com.movie.interceptor;

import com.movie.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    private static final String[] PUBLIC_GET_PREFIXES = {
            "/api/movies", "/api/screenings", "/api/posters",
            "/api/orders/screening", "/api/hall-layouts", "/api/genres"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            String uri = request.getRequestURI();
            for (String prefix : PUBLIC_GET_PREFIXES) {
                if (uri.startsWith(prefix)) {
                    return true;
                }
            }
        }

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.setStatus(403);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":403,\"message\":\"无管理员权限\",\"data\":null}");
            return false;
        }
        return true;
    }
}
