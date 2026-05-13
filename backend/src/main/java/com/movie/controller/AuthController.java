package com.movie.controller;

import com.movie.common.Result;
import com.movie.entity.User;
import com.movie.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestParam @NotBlank String name,
                              @RequestParam @NotBlank String password,
                              @RequestParam(defaultValue = "false") boolean remember,
                              HttpSession session,
                              HttpServletResponse response) {
        User user = userService.login(name, password);
        session.setAttribute("user", user);

        if (remember) {
            Cookie autoLoginCookie = new Cookie("autoLogin", name);
            Cookie savedNameCookie = new Cookie("savedName", name);
            int maxAge = 7 * 24 * 60 * 60;
            autoLoginCookie.setMaxAge(maxAge);
            savedNameCookie.setMaxAge(maxAge);
            autoLoginCookie.setPath("/");
            savedNameCookie.setPath("/");
            response.addCookie(autoLoginCookie);
            response.addCookie(savedNameCookie);
        } else {
            clearRememberCookies(response);
        }

        return Result.success(user);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestParam @NotBlank String name,
                                 @RequestParam @NotBlank String password,
                                 @RequestParam(required = false) String role) {
        User user = userService.register(name, password, role);
        return Result.success("注册成功", user);
    }

    @GetMapping("/me")
    public Result<User> me(HttpServletRequest request, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            String autoLoginName = getCookieValue(request, "autoLogin");
            if (autoLoginName != null && !autoLoginName.isEmpty()) {
                try {
                    User autoUser = userService.getOne(
                            userService.lambdaQuery().eq(User::getName, autoLoginName).getWrapper(), false);
                    if (autoUser != null) {
                        autoUser.setStudentId(null);
                        session.setAttribute("user", autoUser);
                        return Result.success(autoUser);
                    }
                } catch (Exception ignored) {
                }
            }
            return Result.error(401, "未登录");
        }
        return Result.success(user);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();
        clearRememberCookies(response);
        return Result.success(null);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (name.equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    private void clearRememberCookies(HttpServletResponse response) {
        for (String name : new String[]{"autoLogin", "savedName", "savedPassword"}) {
            Cookie c = new Cookie(name, "");
            c.setMaxAge(0);
            c.setPath("/");
            response.addCookie(c);
        }
    }
}
