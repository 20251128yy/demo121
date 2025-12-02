package com.university.restaurant.repository;

import com.university.restaurant.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface DishRepository extends JpaRepository<Dish, Long> {

    // 根据分类查询
    List<Dish> findByCategory(String category);

    // 查询可用菜品
    List<Dish> findByIsAvailableTrue();

    // 根据分类查询可用菜品
    List<Dish> findByCategoryAndIsAvailableTrue(String category);

    // 搜索菜品（名称或描述包含关键字）
    @Query("SELECT d FROM Dish d WHERE d.name LIKE %:keyword% OR d.description LIKE %:keyword%")
    List<Dish> searchDishes(String keyword);
}