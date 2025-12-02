package com.university.restaurant.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "dish")  // 指定表名
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自增主键
    private Long id;

    @Column(nullable = false, length = 100)  // 不能为空，最大长度100
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)  // 10位数字，2位小数
    private BigDecimal price;

    @Column(length = 500)  // 描述可以长一些
    private String description;

    @Column(name = "image_url")  // 数据库列名为image_url
    private String imageUrl;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    // 必须要有无参构造函数（JPA要求）
    public Dish() {}

    // 方便测试的全参构造函数
    public Dish(Long id, String name, BigDecimal price, String description,
                String imageUrl, String category, Boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.isAvailable = isAvailable;
    }
}