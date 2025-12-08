package com.university.restaurant.repository;

import com.university.restaurant.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 根据订单号查找
    Optional<Order> findByOrderNumber(String orderNumber);

    // 根据用户ID查找订单
    List<Order> findByUserId(Long userId);

    // 根据状态查找订单
    List<Order> findByStatus(String status);

    // 根据用户ID和状态查找
    List<Order> findByUserIdAndStatus(Long userId, String status);

    // 根据创建时间范围查找
    List<Order> findByOrderTimeBetween(LocalDateTime start, LocalDateTime end);

    // 根据状态统计数量
    Long countByStatus(String status);

    // 统计用户订单数量
    Long countByUserId(Long userId);

    // 计算订单总额
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.user.id = :userId")
    BigDecimal getTotalAmountByUserId(Long userId);

    // 计算指定状态的订单总额
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    BigDecimal getTotalAmountByUserIdAndStatus(Long userId, String status);
    Page<Order> findAllByUserId(Long userId, Pageable pageable);

    List<Order> findAll(Specification<Order> orderSpecification);
}