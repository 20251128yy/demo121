package com.university.restaurant.repository;

import com.university.restaurant.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 根据订单ID查找订单项
    List<OrderItem> findByOrderId(Long orderId);

    // 根据菜品ID查找订单项
    List<OrderItem> findByDishId(Long dishId);

    // 删除订单项
    void deleteByOrderId(Long orderId);
}