package com.university.restaurant.service;

import com.university.restaurant.dto.CartItemRequest;
import com.university.restaurant.dto.ShoppingCartDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ShoppingCartService {

    // 添加到购物车
    ShoppingCartDTO addToCart(Long userId, CartItemRequest request);

    // 获取用户购物车
    List<ShoppingCartDTO> getUserCart(Long userId);

    // 更新购物车项数量
    ShoppingCartDTO updateCartItem(Long cartItemId, CartItemRequest request);

    // 更新购物车项选中状态
    ShoppingCartDTO updateCartItemSelected(Long cartItemId, Boolean selected);

    // 删除购物车项
    void removeFromCart(Long cartItemId);

    // 清空购物车
    void clearCart(Long userId);

    // 清空选中的购物车项
    void clearSelectedItems(Long userId);

    // 获取购物车总数
    Integer getCartItemCount(Long userId);

    // 获取购物车总价
    BigDecimal getCartTotalAmount(Long userId);

    // 获取选中的购物车项
    List<ShoppingCartDTO> getSelectedCartItems(Long userId);
}