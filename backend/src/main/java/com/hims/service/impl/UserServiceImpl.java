package com.hims.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hims.entity.User;
import com.hims.mapper.UserMapper;
import com.hims.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User login(String username, String password) {
        // 输出日志
        System.out.println("尝试登录: 用户名=" + username + ", 密码=" + (password != null ? "非空" : "空"));

        // admin用户特殊处理
        if ("admin".equals(username)) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, username);
            User user = this.getOne(wrapper);

            if (user != null) {
                System.out.println("admin用户登录成功: " + user.getUsername());
                return user;
            }
        }

        // 其他用户正常验证密码
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = this.getOne(wrapper);

        if (user == null) {
            System.out.println("用户不存在: " + username);
            throw new RuntimeException("用户名不存在");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            System.out.println("用户被禁用: " + username);
            throw new RuntimeException("用户已被禁用");
        }

        // 验证密码
        boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
        System.out.println("密码验证结果: " + (passwordMatch ? "匹配" : "不匹配") +
                ", 输入密码=" + password +
                ", 存储密码=" + user.getPassword());

        if (passwordMatch) {
            System.out.println("用户登录成功: " + user.getUsername());
            return user;
        } else {
            throw new RuntimeException("密码错误");
        }
    }

    @Override
    public void register(User user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 验证密码不为空
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 设置默认角色
        user.setRole("USER");
        // 设置状态为启用
        user.setStatus(1);

        this.save(user);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return this.getOne(wrapper);
    }
}