package com.university.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatistics {
    private Long totalOrders;          // 总订单数
    private Long pendingOrders;         // 待处理订单
    private Long paidOrders;            // 已支付订单
    private Long preparingOrders;       // 准备中订单
    private Long deliveringOrders;      // 配送中订单
    private Long completedOrders;      // 已完成订单
    private Long cancelledOrders;       // 已取消订单

    private BigDecimal totalAmount;     // 总金额
    private BigDecimal pendingAmount;   // 待支付金额
    private BigDecimal paidAmount;      // 已支付金额
    private BigDecimal completedAmount; // 已完成金额

    private LocalDateTime startDate;    // 统计开始时间
    private LocalDateTime endDate;      // 统计结束时间
}