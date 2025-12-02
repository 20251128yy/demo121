
package com.university.restaurant.controller;

import com.university.restaurant.entity.Dish;
import com.university.restaurant.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import java.util.List;

@RestController
@RequestMapping("/api/dishes")
@CrossOrigin(origins = "*") // 允许跨域
public class DishController {

    @Autowired
    private DishService dishService;

    // 获取所有菜品
    @GetMapping
    public List<Dish> getAllDishes() {
        return dishService.getAllDishes();
    }

    // 根据ID获取菜品
    @GetMapping("/{id}")
    public ResponseEntity<Dish> getDishById(@PathVariable Long id) {
        Dish dish = dishService.getDishById(id);
        if (dish != null) {
            return ResponseEntity.ok(dish);
        }
        return ResponseEntity.notFound().build();
    }

    // 根据分类获取菜品
    @GetMapping("/category/{category}")
    public List<Dish> getDishesByCategory(@PathVariable String category) {
        return dishService.getDishesByCategory(category);
    }

    // 获取可用菜品
    @GetMapping("/available")
    public List<Dish> getAvailableDishes() {
        return dishService.getAvailableDishes();
    }

    // 创建菜品
    @PostMapping
    public Dish createDish(@RequestBody Dish dish) {
        return dishService.createDish(dish);
    }

    // 更新菜品
    @PutMapping("/{id}")
    public ResponseEntity<Dish> updateDish(@PathVariable Long id, @RequestBody Dish dishDetails) {
        Dish updatedDish = dishService.updateDish(id, dishDetails);
        if (updatedDish != null) {
            return ResponseEntity.ok(updatedDish);
        }
        return ResponseEntity.notFound().build();
    }

    // 更新菜品状态
    @PatchMapping("/{id}/availability")
    public ResponseEntity<Dish> updateAvailability(@PathVariable Long id, @RequestParam Boolean available) {
        Dish updatedDish = dishService.updateDishAvailability(id, available);
        if (updatedDish != null) {
            return ResponseEntity.ok(updatedDish);
        }
        return ResponseEntity.notFound().build();
    }

    // 删除菜品
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteDish(id);
        return ResponseEntity.ok().build();
    }

    // 搜索菜品
    @GetMapping("/search")
    public List<Dish> searchDishes(@RequestParam String keyword) {
        return dishService.searchDishes(keyword);
    }
}