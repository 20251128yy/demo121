package com.university.restaurant.service;

import com.university.restaurant.entity.User;
import java.util.List;

public interface UserService {

    // 用户注册
    User register(User user);

    // 用户登录
    User login(String username, String password);

    // 获取所有用户
    List<User> getAllUsers();

    // 根据ID获取用户
    User getUserById(Long id);

    // 更新用户信息
    User updateUser(Long id, User userDetails);

    // 删除用户
    void deleteUser(Long id);

    // 更新用户状态
    User updateUserStatus(Long id, Boolean isActive);

    // 检查用户名是否可用
    boolean isUsernameAvailable(String username);

    // 检查手机号是否可用
    boolean isPhoneAvailable(String phone);

    // 检查邮箱是否可用
    boolean isEmailAvailable(String email);

    void logout(Long id);
}