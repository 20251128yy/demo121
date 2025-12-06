package com.university.restaurant.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;      // 用户ID
    private Long dishId;      // 菜品ID
    private Long orderId;     // 订单ID
    private Integer rating;   // 评分 1-5
    private String comment;   // 评价内容
    private LocalDateTime createTime;
}