package com.university.restaurant.service.impl;

import com.university.restaurant.entity.Dish;
import com.university.restaurant.repository.DishRepository;
import com.university.restaurant.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishRepository dishRepository;

    @Override
    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    @Override
    public List<Dish> getDishesByCategory(String category) {
        return dishRepository.findByCategory(category);
    }

    @Override
    public List<Dish> getAvailableDishes() {
        return dishRepository.findByIsAvailableTrue();
    }

    @Override
    public Dish getDishById(Long id) {
        Optional<Dish> dish = dishRepository.findById(id);
        return dish.orElse(null);
    }

    @Override
    public Dish createDish(Dish dish) {
        return dishRepository.save(dish);
    }

    @Override
    public Dish updateDish(Long id, Dish dishDetails) {
        Optional<Dish> optionalDish = dishRepository.findById(id);
        if (optionalDish.isPresent()) {
            Dish dish = optionalDish.get();
            dish.setName(dishDetails.getName());
            dish.setPrice(dishDetails.getPrice());
            dish.setDescription(dishDetails.getDescription());
            dish.setCategory(dishDetails.getCategory());
            dish.setImageUrl(dishDetails.getImageUrl());
            dish.setIsAvailable(dishDetails.getIsAvailable());
            return dishRepository.save(dish);
        }
        return null;
    }

    @Override
    public void deleteDish(Long id) {
        dishRepository.deleteById(id);
    }

    @Override
    public Dish updateDishAvailability(Long id, Boolean isAvailable) {
        Optional<Dish> optionalDish = dishRepository.findById(id);
        if (optionalDish.isPresent()) {
            Dish dish = optionalDish.get();
            dish.setIsAvailable(isAvailable);
            return dishRepository.save(dish);
        }
        return null;
    }

    @Override
    public List<Dish> searchDishes(String keyword) {
        return dishRepository.searchDishes(keyword);
    }
}