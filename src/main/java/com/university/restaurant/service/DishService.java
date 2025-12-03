package com.university.restaurant.service;

import com.university.restaurant.entity.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DishService {
    List<Dish> getAllDishes();
    List<Dish> getDishesByCategory(String category);
    List<Dish> getAvailableDishes();
    Dish getDishById(Long id);
    Dish createDish(Dish dish);
    Dish updateDish(Long id, Dish dishDetails);
    void deleteDish(Long id);
    Dish updateDishAvailability(Long id, Boolean isAvailable);
    List<Dish> searchDishes(String keyword);
    Page<Dish> getDishesByPage(Pageable pageable);
}