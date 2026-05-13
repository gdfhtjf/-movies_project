package com.movie.config;

import com.movie.entity.User;
import com.movie.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;

    @Override
    public void run(String... args) {
        initAdminUser();
        ensureBCryptPasswords();
    }

    private void initAdminUser() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName, "Admin");
        User admin = userMapper.selectOne(wrapper);
        if (admin == null) {
            admin = new User();
            admin.setName("Admin");
            admin.setStudentId(BCrypt.hashpw("123456", BCrypt.gensalt()));
            admin.setRole("admin");
            admin.setCreateTime(LocalDateTime.now());
            userMapper.insert(admin);
            log.info("管理员账号已创建: Admin / 123456");
        }
    }

    private void ensureBCryptPasswords() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(User::getStudentId);
        var users = userMapper.selectList(wrapper);
        for (User user : users) {
            if (!user.getStudentId().startsWith("$2a$") && !user.getStudentId().startsWith("$2b$")) {
                user.setStudentId(BCrypt.hashpw(user.getStudentId(), BCrypt.gensalt()));
                userMapper.updateById(user);
                log.info("用户 {} 的密码已升级为BCrypt加密", user.getName());
            }
        }
    }
}
