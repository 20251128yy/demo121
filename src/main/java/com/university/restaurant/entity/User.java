package com.university.restaurant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_logged_in",nullable = false)
    private  Boolean isLoggedIn = false;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度3-20个字符")
    @Column(unique = true, nullable = false, length = 20)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码至少6位")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Column(unique = true, nullable = false, length = 11)
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "角色不能为空")
    @Column(nullable = false, length = 20)
    private String role = "CUSTOMER"; // CUSTOMER, ADMIN

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // 无参构造函数
    public User() {}

    // 注册用构造函数
    public User(String username, String password, String phone, String email) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.role = "CUSTOMER";
        this.isActive = true;
        this.createTime = LocalDateTime.now();
    }
    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();
}