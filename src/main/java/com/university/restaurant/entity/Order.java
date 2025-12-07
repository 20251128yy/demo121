package com.university.restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;  // 订单号

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;           // 用户

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;  // 订单总金额

    @Column(length = 20)
    private String status = "PENDING";  // 状态: PENDING, PAID, PREPARING, DELIVERING, COMPLETED, CANCELLED

    @Column(length = 200)
    private String address;  // 配送地址

    @Column(length = 20)
    private String phone;   // 联系电话

    @Column(length = 500)
    private String note;   // 备注

    @CreationTimestamp

    @Column(name = "order_time", updatable =false)
    private LocalDateTime orderTime;  // 下单时间

    @Column(name = "paid_time")
    private LocalDateTime paidTime;    // 支付时间

    @Column(name = "preparing_time")
    private LocalDateTime preparingTime;  // 准备时间

    @Column(name = "delivering_time")
    private LocalDateTime deliveringTime;  // 配送时间

    @Column(name = "complete_time")
    private LocalDateTime completeTime;    // 完成时间

    @Column(name = "cancel_time")
    private LocalDateTime cancelTime;      // 取消时间

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();  // 订单项列表

    // 订单号生成
    @PrePersist
    public void generateOrderNumber() {
        if (this.orderNumber == null) {
            this.orderNumber = "ORD" + System.currentTimeMillis() + (int)(Math.random() * 1000);
        }
    }
}