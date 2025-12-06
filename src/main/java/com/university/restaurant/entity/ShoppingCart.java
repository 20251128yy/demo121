package com.university.restaurant.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shopping_cart")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;      // 用户ID
    private Long dishId;      // 菜品ID
    private Integer quantity; // 数量
    private LocalDateTime addTime; // 添加时间
}