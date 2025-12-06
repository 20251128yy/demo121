package com.university.restaurant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "dish")
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "菜品名称不能为空")
    @Size(min = 2, max = 50, message = "菜品名称长度必须在2-50个字符之间")
    @Column(nullable = false, length = 50)
    private String name;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "价格必须大于0")
    @Digits(integer = 10, fraction = 2, message = "价格格式不正确")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Size(max = 500, message = "描述不能超过500个字符")
    @Column(length = 500)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @NotBlank(message = "分类不能为空")
    @Size(max = 50, message = "分类名称不能超过50个字符")
    @Column(nullable = false, length = 50)
    private String category;

    @NotNull(message = "可用状态不能为空")
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    // 无参构造函数（JPA要求）
    public Dish() {}

    // 全参构造函数（方便测试）
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
    @OneToMany(mappedBy = "dish")
    private List<OrderItem> orderItems = new ArrayList<>();
}