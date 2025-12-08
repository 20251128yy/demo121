package com.university.restaurant.repository;

import com.university.restaurant.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    // 根据用户ID查找购物车项
    List<ShoppingCart> findByUserId(Long userId);

    // 根据用户ID和菜品ID查找
    Optional<ShoppingCart> findByUserIdAndDishId(Long userId, Long dishId);

    // 根据用户ID和选中状态查找
    List<ShoppingCart> findByUserIdAndSelected(Long userId, Boolean selected);

    // 删除用户购物车
    @Transactional
    @Modifying
    @Query("DELETE FROM ShoppingCart s WHERE s.user.id = :userId")
    void deleteByUserId(Long userId);

    // 删除选中的购物车项
    @Transactional
    @Modifying
    @Query("DELETE FROM ShoppingCart s WHERE s.user.id = :userId AND s.selected = true")
    void deleteSelectedByUserId(Long userId);
}