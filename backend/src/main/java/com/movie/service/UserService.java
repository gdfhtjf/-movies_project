package com.movie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.entity.User;

public interface UserService extends IService<User> {

    User login(String name, String password);

    User register(String name, String password);

    User register(String name, String password, String role);

    void changePassword(Integer userId, String oldPassword, String newPassword);

    void adminResetPassword(Integer userId, String newPassword);

    Page<User> pageUsers(int current, int size, String keyword);
}
