package com.university.restaurant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shopping_cart")
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 用户
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 菜品
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    // 数量
    @Column(nullable = false)
    private Integer quantity = 1;

    // 选择的规格（可选）
    @Column(length = 50)
    private String specification;

    // 备注
    @Column(length = 200)
    private String note;

    // 是否选中
    @Column(nullable = false)
    private Boolean selected = true;

    // 创建时间
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    // 更新时间
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // 计算小计
    public BigDecimal getSubtotal() {
        if (dish != null && dish.getPrice() != null && quantity != null) {
            return dish.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}