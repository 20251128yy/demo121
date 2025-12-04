package com.university.restaurant.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private String role;
    private LocalDateTime createTime;
    private LocalDateTime lastLoginTime;
    private Boolean isActive;

    // 从User实体转换
    public static UserDTO fromEntity(com.university.restaurant.entity.User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setCreateTime(user.getCreateTime());
        dto.setLastLoginTime(user.getLastLoginTime());
        dto.setIsActive(user.getIsActive());
        return dto;
    }
}