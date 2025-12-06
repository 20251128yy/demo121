package com.university.restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联到用户
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 订单号
    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;

    // 订单状态
    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, PAID, PREPARING, DELIVERING, COMPLETED, CANCELLED

    // 总金额
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // 配送地址
    @Column(length = 200)
    private String address;

    // 联系电话
    @Column(length = 20)
    private String phone;

    // 备注
    @Column(length = 500)
    private String note;

    // 下单时间
    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime = LocalDateTime.now();

    // 完成时间
    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    // 订单项
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
}