package com.university.restaurant.controller;

import com.university.restaurant.entity.Dish;
import com.university.restaurant.service.DishService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dishes")
@CrossOrigin(origins = "*")
public class DishController {

    @Autowired
    private DishService dishService;

    // 获取所有菜品
    @GetMapping
    public ResponseEntity<List<Dish>> getAllDishes() {
        List<Dish> dishes = dishService.getAllDishes();
        return ResponseEntity.ok(dishes);
    }
    // 分页查询菜品
    @GetMapping("/page")
    public ResponseEntity<Page<Dish>> getDishesByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Dish> dishPage = dishService.getDishesByPage(pageable);
        return ResponseEntity.ok(dishPage);
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

    // 创建菜品 - 添加@Valid注解进行验证
    @PostMapping
    public ResponseEntity<?> createDish(@Valid @RequestBody Dish dish) {
        try {
            Dish savedDish = dishService.createDish(dish);
            return ResponseEntity.ok(savedDish);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "创建菜品失败: " + e.getMessage())
            );
        }
    }

    // 更新菜品 - 添加验证
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDish(@PathVariable Long id, @Valid @RequestBody Dish dishDetails) {
        try {
            Dish updatedDish = dishService.updateDish(id, dishDetails);
            if (updatedDish != null) {
                return ResponseEntity.ok(updatedDish);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "更新菜品失败: " + e.getMessage())
            );
        }
    }

    // 其他方法保持不变...
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Dish>> getDishesByCategory(@PathVariable String category) {
        List<Dish> dishes = dishService.getDishesByCategory(category);
        return ResponseEntity.ok(dishes);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Dish>> getAvailableDishes() {
        List<Dish> dishes = dishService.getAvailableDishes();
        return ResponseEntity.ok(dishes);
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<Dish> updateAvailability(@PathVariable Long id, @RequestParam Boolean available) {
        Dish updatedDish = dishService.updateDishAvailability(id, available);
        if (updatedDish != null) {
            return ResponseEntity.ok(updatedDish);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteDish(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Dish>> searchDishes(@RequestParam String keyword) {
        List<Dish> dishes = dishService.searchDishes(keyword);
        return ResponseEntity.ok(dishes);
    }


}