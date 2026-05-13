package com.movie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.movie.common.Result;
import com.movie.entity.User;
import com.movie.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Result<Page<User>> list(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String keyword) {
        return Result.success(userService.pageUsers(page, size, keyword));
    }

    @GetMapping("/{id}")
    public Result<User> detail(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setStudentId(null);
        return Result.success(user);
    }

    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Integer id, @RequestBody User user) {
        User existing = userService.getById(id);
        if (existing == null) {
            return Result.error(404, "用户不存在");
        }
        existing.setName(user.getName());
        existing.setRole(user.getRole());
        userService.updateById(existing);
        existing.setStudentId(null);
        return Result.success("更新成功", existing);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        if ("admin".equals(user.getRole())) {
            return Result.error(400, "不能删除管理员账户");
        }
        userService.removeById(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/{id}/admin-reset-password")
    public Result<Void> adminResetPassword(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String newPassword = body.get("newPassword");
        if (newPassword == null || newPassword.isBlank()) {
            return Result.error(400, "新密码不能为空");
        }
        userService.adminResetPassword(id, newPassword);
        return Result.success("密码重置成功", null);
    }

    @PutMapping("/{id}/password")
    public Result<Void> changePassword(@PathVariable Integer id,
                                       @RequestBody Map<String, String> body,
                                       HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (!currentUser.getId().equals(id) && !"admin".equals(currentUser.getRole())) {
            return Result.error(403, "无权修改他人密码");
        }
        userService.changePassword(id, body.get("oldPassword"), body.get("newPassword"));
        return Result.success("密码修改成功", null);
    }

    @PutMapping("/{id}/profile")
    public Result<User> updateProfile(@PathVariable Integer id,
                                       @RequestBody Map<String, String> body,
                                       HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return Result.error(401, "请先登录");
        }
        if (!currentUser.getId().equals(id) && !"admin".equals(currentUser.getRole())) {
            return Result.error(403, "无权修改他人资料");
        }
        User existing = userService.getById(id);
        if (existing == null) {
            return Result.error(404, "用户不存在");
        }
        if (body.containsKey("name")) {
            existing.setName(body.get("name"));
        }
        if (body.containsKey("email")) {
            existing.setEmail(body.get("email"));
        }
        if (body.containsKey("phone")) {
            existing.setPhone(body.get("phone"));
        }
        userService.updateById(existing);
        existing.setStudentId(null);
        return Result.success("资料更新成功", existing);
    }
}
