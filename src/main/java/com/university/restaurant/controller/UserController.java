package com.university.restaurant.controller;

import com.university.restaurant.dto.UserDTO;
import com.university.restaurant.dto.UserUpdateDTO;
import com.university.restaurant.entity.User;
import com.university.restaurant.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // 用户注册 - 使用DTO过滤密码
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            UserDTO userDTO = UserDTO.fromEntity(registeredUser); // 转换为DTO
            return ResponseEntity.ok(createSuccessResponse("注册成功", userDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 用户登录 - 使用DTO过滤密码
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            User user = userService.login(username, password);
            UserDTO userDTO = UserDTO.fromEntity(user); // 转换为DTO
            return ResponseEntity.ok(createSuccessResponse("登录成功", userDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 检查用户名是否可用
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        boolean available = userService.isUsernameAvailable(username);
        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        response.put("message", available ? "用户名可用" : "用户名已存在");
        return ResponseEntity.ok(response);
    }

    // 获取所有用户 - 使用DTO过滤密码
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // 转换为DTO列表
        List<UserDTO> userDTOs = users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    // 根据ID获取用户 - 使用DTO过滤密码
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            UserDTO userDTO = UserDTO.fromEntity(user); // 转换为DTO
            return ResponseEntity.ok(userDTO);
        }
        return ResponseEntity.notFound().build();
    }

    // 更新用户信息 - 使用专门的UpdateDTO
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            // 创建User对象，只设置可更新的字段
            User userDetails = new User();
            userDetails.setPhone(userUpdateDTO.getPhone());
            userDetails.setEmail(userUpdateDTO.getEmail());

            User updatedUser = userService.updateUser(id, userDetails);
            if (updatedUser != null) {
                UserDTO userDTO = UserDTO.fromEntity(updatedUser);
                return ResponseEntity.ok(createSuccessResponse("更新成功", userDTO));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // 更新用户状态 - 使用DTO过滤密码
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestParam Boolean active) {
        User updatedUser = userService.updateUserStatus(id, active);
        if (updatedUser != null) {
            String message = active ? "用户已启用" : "用户已禁用";
            UserDTO userDTO = UserDTO.fromEntity(updatedUser);
            return ResponseEntity.ok(createSuccessResponse(message, userDTO));
        }
        return ResponseEntity.notFound().build();
    }

    // 添加注销功能
    @PostMapping("/{id}/logout")
    public ResponseEntity<?> logout(@PathVariable Long id) {
        try {
            userService.logout(id);
            return ResponseEntity.ok()
                    .header("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\"")
                    .body(createSuccessResponse("注销成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 工具方法：创建成功响应
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    // 工具方法：创建错误响应
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}