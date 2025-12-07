package com.university.restaurant.service.impl;

import com.university.restaurant.dto.OrderDTO;
import com.university.restaurant.dto.OrderRequest;
import com.university.restaurant.entity.*;
import com.university.restaurant.repository.*;
import com.university.restaurant.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DishRepository dishRepository;

    @Override
    public OrderDTO createOrder(OrderRequest orderRequest) {
        // 1. 验证用户
        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 2. 创建订单
        Order order = new Order();
        order.setUser(user);
        order.setAddress(orderRequest.getAddress());
        order.setPhone(orderRequest.getPhone());
        order.setNote(orderRequest.getNote());
        order.setStatus("PENDING");
        order.setOrderTime(LocalDateTime.now());

        // 3. 计算总金额并创建订单项
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(item -> {
                    Dish dish = dishRepository.findById(item.getDishId())
                            .orElseThrow(() -> new RuntimeException("菜品不存在: " + item.getDishId()));

                    if (!dish.getIsAvailable()) {
                        throw new RuntimeException("菜品已下架: " + dish.getName());
                    }

                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setDish(dish);
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPrice(dish.getPrice());

                    return orderItem;
                })
                .collect(Collectors.toList());

        // 计算总金额
        for (OrderItem item : orderItems) {
            totalAmount = totalAmount.add(item.getSubtotal());
        }
        order.setTotalAmount(totalAmount);

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        // 保存订单项
        orderItems.forEach(item -> {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        });
        savedOrder.setOrderItems(orderItems);

        return OrderDTO.fromEntity(savedOrder);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        return OrderDTO.fromEntity(order);
    }

    @Override
    public OrderDTO getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        return OrderDTO.fromEntity(order);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        String oldStatus = order.getStatus();
        order.setStatus(status);

        // 更新时间
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case "PAID":
                order.setPaidTime(now);
                break;
            case "PREPARING":
                order.setPreparingTime(now);
                break;
            case "DELIVERING":
                order.setDeliveringTime(now);
                break;
            case "COMPLETED":
                order.setCompleteTime(now);
                break;
            case "CANCELLED":
                order.setCancelTime(now);
                break;
        }

        Order updatedOrder = orderRepository.save(order);
        return OrderDTO.fromEntity(updatedOrder);
    }

    @Override
    public OrderDTO cancelOrder(Long id, String reason) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("只有待支付订单可以取消");
        }

        order.setStatus("CANCELLED");
        order.setCancelTime(LocalDateTime.now());
        if (reason != null && !reason.isEmpty()) {
            order.setNote((order.getNote() != null ? order.getNote() + "。取消原因：" : "取消原因：") + reason);
        }

        Order cancelledOrder = orderRepository.save(order);
        return OrderDTO.fromEntity(cancelledOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("只能删除已取消的订单");
        }

        orderRepository.deleteById(id);
    }

    @Override
    public OrderStats getOrderStats(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        long totalOrders = orders.size();
        BigDecimal totalAmount = orders.stream()
                .filter(o -> "COMPLETED".equals(o.getStatus()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingCount = orders.stream()
                .filter(o -> "PENDING".equals(o.getStatus()))
                .count();

        long completedCount = orders.stream()
                .filter(o -> "COMPLETED".equals(o.getStatus()))
                .count();

        return new OrderStats(totalOrders, totalAmount, pendingCount, completedCount);
    }
}