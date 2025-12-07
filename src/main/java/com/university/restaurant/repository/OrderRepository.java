package com.university.restaurant.repository;

import com.university.restaurant.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 根据用户ID查询订单
    List<Order> findByUserId(Long userId);

    // 根据订单号查询
    Optional<Order> findByOrderNumber(String orderNumber);

    // 根据状态查询订单
    List<Order> findByStatus(String status);

    // 根据用户ID和状态查询
    List<Order> findByUserIdAndStatus(Long userId, String status);

    // 统计订单数量
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    Long countByUserId(Long userId);

    // 计算用户总消费
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.user.id = :userId AND o.status = 'COMPLETED'")
    BigDecimal sumTotalAmountByUserId(Long userId);
}