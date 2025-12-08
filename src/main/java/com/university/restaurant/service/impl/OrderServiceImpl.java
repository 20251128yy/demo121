package com.university.restaurant.service.impl;

import com.university.restaurant.dto.*;
import com.university.restaurant.entity.*;
import com.university.restaurant.repository.*;
import com.university.restaurant.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Autowired
    private final OrderRepository orderRepository;
    @Autowired
    private final OrderItemRepository orderItemRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final DishRepository dishRepository;

    // 创建订单
    @Override
    public OrderDTO createOrder(OrderRequest orderRequest) {
        // 验证用户
        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 验证订单项
        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            throw new RuntimeException("订单项不能为空");
        }

        // 创建订单
        Order order = new Order();
        order.setUser(user);
        order.setAddress(orderRequest.getAddress());
        order.setPhone(orderRequest.getPhone());
        order.setNote(orderRequest.getNote());
        order.setStatus("PENDING");  // 待支付
        order.setOrderTime(LocalDateTime.now());

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderRequest.OrderItemRequest itemRequest : orderRequest.getItems()) {
            Dish dish = dishRepository.findById(itemRequest.getDishId())
                    .orElseThrow(() -> new RuntimeException("菜品不存在: " + itemRequest.getDishId()));

            if (!dish.getIsAvailable()) {
                throw new RuntimeException("菜品已下架: " + dish.getName());
            }

            if (itemRequest.getQuantity() <= 0) {
                throw new RuntimeException("菜品数量必须大于0: " + dish.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setDish(dish);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(dish.getPrice());

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        // 保存订单项
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }

        return OrderDTO.fromEntity(savedOrder);
    }

    // 获取订单详情
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

    // 查询订单
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
    public List<OrderDTO> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status).stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByUserAndStatus(Long userId, String status) {
        return orderRepository.findByUserIdAndStatus(userId, status).stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 分页查询
    @Override
    public Page<OrderDTO> getOrdersByPage(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(OrderDTO::fromEntity);
    }

    @Override
    public Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("orderTime").descending()
        );
        return orderRepository.findAllByUserId(userId, pageRequest)
                .map(OrderDTO::fromEntity);
    }

    // 更新状态
    @Override
    public OrderDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!isValidStatusTransition(order.getStatus(), status)) {
            throw new RuntimeException("无效的状态转换");
        }

        order.setStatus(status);
        updateStatusTime(order, status);

        Order updatedOrder = orderRepository.save(order);
        return OrderDTO.fromEntity(updatedOrder);
    }

    // 支付订单
    @Override
    public OrderDTO payOrder(Long id, String paymentMethod) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("只有待支付订单可以支付");
        }

        order.setStatus("PAID");
        order.setPaidTime(LocalDateTime.now());
        // TODO: 集成支付接口

        Order paidOrder = orderRepository.save(order);
        return OrderDTO.fromEntity(paidOrder);
    }

    // 取消订单
    @Override
    public OrderDTO cancelOrder(Long id, String reason) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"PENDING".equals(order.getStatus()) && !"PAID".equals(order.getStatus())) {
            throw new RuntimeException("当前状态的订单不能取消");
        }

        order.setStatus("CANCELLED");
        order.setCancelTime(LocalDateTime.now());
        if (StringUtils.hasText(reason)) {
            order.setNote(order.getNote() + (order.getNote() != null ? " | " : "") + "取消原因: " + reason);
        }

        Order cancelledOrder = orderRepository.save(order);
        return OrderDTO.fromEntity(cancelledOrder);
    }

    // 完成订单
    @Override
    public OrderDTO completeOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"DELIVERING".equals(order.getStatus())) {
            throw new RuntimeException("只有配送中的订单可以完成");
        }

        order.setStatus("COMPLETED");
        order.setCompleteTime(LocalDateTime.now());

        Order completedOrder = orderRepository.save(order);
        return OrderDTO.fromEntity(completedOrder);
    }

    // 开始配送
    @Override
    public OrderDTO deliverOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"PREPARING".equals(order.getStatus())) {
            throw new RuntimeException("只有准备中的订单可以开始配送");
        }

        order.setStatus("DELIVERING");
        order.setDeliveringTime(LocalDateTime.now());

        Order deliveringOrder = orderRepository.save(order);
        return OrderDTO.fromEntity(deliveringOrder);
    }

    // 删除订单
    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("只能删除已取消的订单");
        }

        orderRepository.delete(order);
    }

    // 搜索订单
    @Override
    public List<OrderDTO> searchOrders(String keyword) {
        List<Order> orders = orderRepository.findAll((Specification<Order>) (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(keyword)) {
                return null;
            }

            String likeKeyword = "%" + keyword + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("orderNumber"), likeKeyword),
                    criteriaBuilder.like(root.get("address"), likeKeyword),
                    criteriaBuilder.like(root.get("phone"), likeKeyword),
                    criteriaBuilder.like(root.get("note"), likeKeyword)
            );
        });

        return orders.stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 统计
    @Override
    public OrderStatistics getOrderStatistics() {
        return getOrderStatistics(null, null, null);
    }

    @Override
    public OrderStatistics getOrderStatisticsByUserId(Long userId) {
        return getOrderStatistics(userId, null, null);
    }

    @Override
    public OrderStatistics getOrderStatisticsByDateRange(LocalDateTime start, LocalDateTime end) {
        return getOrderStatistics(null, start, end);
    }

    private OrderStatistics getOrderStatistics(Long userId, LocalDateTime start, LocalDateTime end) {
        List<Order> orders = getFilteredOrders(userId, start, end);

        OrderStatistics stats = new OrderStatistics();
        stats.setTotalOrders((long) orders.size());
        stats.setPendingOrders(orders.stream().filter(o -> "PENDING".equals(o.getStatus())).count());
        stats.setPaidOrders(orders.stream().filter(o -> "PAID".equals(o.getStatus())).count());
        stats.setPreparingOrders(orders.stream().filter(o -> "PREPARING".equals(o.getStatus())).count());
        stats.setDeliveringOrders(orders.stream().filter(o -> "DELIVERING".equals(o.getStatus())).count());
        stats.setCompletedOrders(orders.stream().filter(o -> "COMPLETED".equals(o.getStatus())).count());
        stats.setCancelledOrders(orders.stream().filter(o -> "CANCELLED".equals(o.getStatus())).count());

        stats.setTotalAmount(orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        stats.setPendingAmount(orders.stream()
                .filter(o -> "PENDING".equals(o.getStatus()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        stats.setPaidAmount(orders.stream()
                .filter(o -> "PAID".equals(o.getStatus()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        stats.setCompletedAmount(orders.stream()
                .filter(o -> "COMPLETED".equals(o.getStatus()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        stats.setStartDate(start);
        stats.setEndDate(end);

        return stats;
    }

    private List<Order> getFilteredOrders(Long userId, LocalDateTime start, LocalDateTime end) {
        if (userId != null) {
            return orderRepository.findByUserId(userId);
        } else if (start != null && end != null) {
            return orderRepository.findByOrderTimeBetween(start, end);
        } else {
            return orderRepository.findAll();
        }
    }

    // 状态转换验证
    private boolean isValidStatusTransition(String from, String to) {
        Map<String, List<String>> validTransitions = Map.of(
                "PENDING", Arrays.asList("PAID", "CANCELLED"),
                "PAID", Arrays.asList("PREPARING", "CANCELLED"),
                "PREPARING", Arrays.asList("DELIVERING"),
                "DELIVERING", Arrays.asList("COMPLETED"),
                "COMPLETED", List.of(),
                "CANCELLED", List.of()
        );

        return validTransitions.getOrDefault(from, List.of()).contains(to);
    }

    // 更新状态时间
    private void updateStatusTime(Order order, String status) {
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
    }
}