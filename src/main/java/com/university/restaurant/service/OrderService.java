package com.university.restaurant.service;

import com.university.restaurant.dto.OrderDTO;
import com.university.restaurant.dto.OrderRequest;
import com.university.restaurant.dto.OrderStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    // 创建订单
    OrderDTO createOrder(OrderRequest orderRequest);

    // 获取订单
    OrderDTO getOrderById(Long id);
    OrderDTO getOrderByNumber(String orderNumber);

    // 查询订单
    List<OrderDTO> getAllOrders();
    List<OrderDTO> getOrdersByUserId(Long userId);
    List<OrderDTO> getOrdersByStatus(String status);
    List<OrderDTO> getOrdersByUserAndStatus(Long userId, String status);

    // 分页查询
    Page<OrderDTO> getOrdersByPage(Pageable pageable);
    Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable);

    // 更新状态
    OrderDTO updateOrderStatus(Long id, String status);
    OrderDTO payOrder(Long id, String paymentMethod);
    OrderDTO cancelOrder(Long id, String reason);
    OrderDTO completeOrder(Long id);
    OrderDTO deliverOrder(Long id);

    // 删除订单
    void deleteOrder(Long id);

    // 统计
    OrderStatistics getOrderStatistics();
    OrderStatistics getOrderStatisticsByUserId(Long userId);
    OrderStatistics getOrderStatisticsByDateRange(LocalDateTime start, LocalDateTime end);

    // 搜索
    List<OrderDTO> searchOrders(String keyword);
}