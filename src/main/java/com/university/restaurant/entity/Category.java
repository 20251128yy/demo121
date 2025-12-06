package com.university.restaurant.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;       // 分类名称
    private String description; // 描述
    private Integer sort;      // 排序
    private Boolean isActive; // 是否启用
}