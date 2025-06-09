package com.hims.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hims.entity.User;

public interface UserService extends IService<User> {
    User login(String username, String password);

    void register(User user);

    void updatePassword(Long userId, String oldPassword, String newPassword);

    User getByUsername(String username);
}