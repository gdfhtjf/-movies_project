package com.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.entity.User;
import com.movie.mapper.UserMapper;
import com.movie.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User login(String name, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName, name);
        User user = getOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (!BCrypt.checkpw(password, user.getStudentId())) {
            throw new RuntimeException("用户名或密码错误");
        }
        user.setStudentId(null);
        return user;
    }

    @Override
    @Transactional
    public User register(String name, String password) {
        return register(name, password, "user");
    }

    @Override
    @Transactional
    public User register(String name, String password, String role) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName, name);
        if (getOne(wrapper) != null) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setName(name);
        user.setStudentId(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setRole("admin".equals(role) ? "admin" : "user");
        user.setCreateTime(LocalDateTime.now());
        save(user);
        user.setStudentId(null);
        return user;
    }

    @Override
    @Transactional
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!BCrypt.checkpw(oldPassword, user.getStudentId())) {
            throw new RuntimeException("原密码错误");
        }
        user.setStudentId(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        updateById(user);
    }

    @Override
    @Transactional
    public void adminResetPassword(Integer userId, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStudentId(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        updateById(user);
    }

    @Override
    public Page<User> pageUsers(int current, int size, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getName, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> page = page(new Page<>(current, size), wrapper);
        page.getRecords().forEach(u -> u.setStudentId(null));
        return page;
    }
}
