package com.university.restaurant.controller;

import com.university.restaurant.dto.CartItemRequest;
import com.university.restaurant.dto.ShoppingCartDTO;
import com.university.restaurant.service.ShoppingCartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ShoppingCartController {

    @Autowired
    private final ShoppingCartService shoppingCartService;

    // 获取用户购物车
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCart(@PathVariable Long userId) {
        try {
            List<ShoppingCartDTO> cartItems = shoppingCartService.getUserCart(userId);
            return ResponseEntity.ok(createSuccessResponse("获取成功", cartItems));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 添加到购物车
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> addToCart(
            @PathVariable Long userId,
            @Valid @RequestBody CartItemRequest request) {
        try {
            ShoppingCartDTO cartItem = shoppingCartService.addToCart(userId, request);
            return ResponseEntity.ok(createSuccessResponse("已添加到购物车", cartItem));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 更新购物车项
    @PutMapping("/{cartItemId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long cartItemId,
            @Valid @RequestBody CartItemRequest request) {
        try {
            ShoppingCartDTO updatedItem = shoppingCartService.updateCartItem(cartItemId, request);
            return ResponseEntity.ok(createSuccessResponse("更新成功", updatedItem));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 更新选中状态
    @PatchMapping("/{cartItemId}/select")
    public ResponseEntity<?> updateSelected(
            @PathVariable Long cartItemId,
            @RequestParam Boolean selected) {
        try {
            ShoppingCartDTO updatedItem = shoppingCartService.updateCartItemSelected(cartItemId, selected);
            return ResponseEntity.ok(createSuccessResponse("更新成功", updatedItem));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 删除购物车项
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<?> removeCartItem(@PathVariable Long cartItemId) {
        try {
            shoppingCartService.removeFromCart(cartItemId);
            return ResponseEntity.ok(createSuccessResponse("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 清空购物车
    @DeleteMapping("/user/{userId}/clear")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        try {
            shoppingCartService.clearCart(userId);
            return ResponseEntity.ok(createSuccessResponse("购物车已清空", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 清空选中项
    @DeleteMapping("/user/{userId}/clear-selected")
    public ResponseEntity<?> clearSelectedItems(@PathVariable Long userId) {
        try {
            shoppingCartService.clearSelectedItems(userId);
            return ResponseEntity.ok(createSuccessResponse("已清空选中项", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 获取购物车信息
    @GetMapping("/user/{userId}/info")
    public ResponseEntity<?> getCartInfo(@PathVariable Long userId) {
        try {
            List<ShoppingCartDTO> cartItems = shoppingCartService.getUserCart(userId);
            Integer itemCount = shoppingCartService.getCartItemCount(userId);
            BigDecimal totalAmount = shoppingCartService.getCartTotalAmount(userId);

            Map<String, Object> data = new HashMap<>();
            data.put("items", cartItems);
            data.put("itemCount", itemCount);
            data.put("totalAmount", totalAmount);
            data.put("selectedCount", cartItems.stream()
                    .filter(ShoppingCartDTO::getSelected)
                    .count());

            return ResponseEntity.ok(createSuccessResponse("获取成功", data));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 工具方法
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}