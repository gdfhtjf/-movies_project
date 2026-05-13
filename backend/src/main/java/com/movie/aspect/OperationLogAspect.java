package com.movie.aspect;

import com.movie.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping) || "
            + "@annotation(org.springframework.web.bind.annotation.PutMapping) || "
            + "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object logCUD(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attrs.getRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = getClientIp(request);
        String username = getUsername(request);
        String operation = getOperationName(joinPoint);

        Object[] args = joinPoint.getArgs();
        Object result = joinPoint.proceed();
        long elapsed = System.currentTimeMillis() - start;

        String resultStatus = "成功";
        if (result instanceof Result<?> r) {
            if (r.getCode() != 200) {
                resultStatus = "失败(" + r.getCode() + "): " + r.getMessage();
            }
        }

        log.info("[操作日志] {} | {} | {} | {} | IP:{} | 参数:{}(已脱敏) | 耗时:{}ms | {}",
                LocalDateTime.now().format(DT_FMT),
                username,
                method,
                uri,
                ip,
                args != null ? args.length + "个" : "0",
                elapsed,
                resultStatus);

        return result;
    }

    private String getOperationName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PostMapping.class)) {
            return "创建";
        } else if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PutMapping.class)) {
            return "更新";
        } else if (method.isAnnotationPresent(org.springframework.web.bind.annotation.DeleteMapping.class)) {
            return "删除";
        }
        return "操作";
    }

    private String getUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object user = session.getAttribute("user");
            if (user != null) {
                try {
                    Method getName = user.getClass().getMethod("getName");
                    return (String) getName.invoke(user);
                } catch (Exception ignored) {
                }
            }
        }
        return "匿名用户";
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
