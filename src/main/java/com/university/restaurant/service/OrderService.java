package com.university.restaurant.service;

import com.university.restaurant.dto.OrderDTO;
import com.university.restaurant.dto.OrderRequest;
import com.university.restaurant.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    // 创建订单
    OrderDTO createOrder(OrderRequest orderRequest);

    // 根据ID获取订单
    OrderDTO getOrderById(Long id);

    // 根据订单号获取订单
    OrderDTO getOrderByNumber(String orderNumber);

    // 获取所有订单
    List<OrderDTO> getAllOrders();

    // 根据用户ID获取订单
    List<OrderDTO> getOrdersByUserId(Long userId);

    // 更新订单状态
    OrderDTO updateOrderStatus(Long id, String status);

    // 取消订单
    OrderDTO cancelOrder(Long id, String reason);

    // 删除订单
    void deleteOrder(Long id);

    // 获取订单统计
    OrderStats getOrderStats(Long userId);

    // 订单统计信息
    record OrderStats(
            Long totalOrders,
            BigDecimal totalAmount,
            Long pendingCount,
            Long completedCount
    ) {}
}