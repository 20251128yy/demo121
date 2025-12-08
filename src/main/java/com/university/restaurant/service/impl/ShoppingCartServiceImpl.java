package com.university.restaurant.service.impl;

import com.university.restaurant.dto.CartItemRequest;
import com.university.restaurant.dto.OrderDTO;
import com.university.restaurant.dto.OrderRequest;
import com.university.restaurant.dto.ShoppingCartDTO;
import com.university.restaurant.entity.*;
import com.university.restaurant.repository.*;
import com.university.restaurant.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final DishRepository dishRepository;

    @Override
    public ShoppingCartDTO addToCart(Long userId, CartItemRequest request) {
        // 验证用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 验证菜品
        Dish dish = dishRepository.findById(request.getDishId())
                .orElseThrow(() -> new RuntimeException("菜品不存在"));

        if (!dish.getIsAvailable()) {
            throw new RuntimeException("菜品已下架");
        }

        // 检查是否已存在
        ShoppingCart existingItem = shoppingCartRepository
                .findByUserIdAndDishId(userId, request.getDishId()).orElse(null);

        if (existingItem != null) {
            // 更新数量
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            existingItem.setUpdateTime(LocalDateTime.now());
            if (StringUtils.hasText(request.getNote())) {
                existingItem.setNote(request.getNote());
            }
            if (request.getSelected() != null) {
                existingItem.setSelected(request.getSelected());
            }
            ShoppingCart saved = shoppingCartRepository.save(existingItem);
            return convertToDTO(saved);
        } else {
            // 新增
            ShoppingCart cartItem = new ShoppingCart();
            cartItem.setUser(user);
            cartItem.setDish(dish);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setSpecification(request.getSpecification());
            cartItem.setNote(request.getNote());
            cartItem.setSelected(request.getSelected() != null ? request.getSelected() : true);
            cartItem.setCreateTime(LocalDateTime.now());
            cartItem.setUpdateTime(LocalDateTime.now());

            ShoppingCart saved = shoppingCartRepository.save(cartItem);
            return convertToDTO(saved);
        }
    }

    @Override
    public List<ShoppingCartDTO> getUserCart(Long userId) {
        List<ShoppingCart> cartItems = shoppingCartRepository.findByUserId(userId);
        return cartItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ShoppingCartDTO updateCartItem(Long cartItemId, CartItemRequest request) {
        ShoppingCart cartItem = shoppingCartRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("购物车项不存在"));

        cartItem.setQuantity(request.getQuantity());
        if (StringUtils.hasText(request.getSpecification())) {
            cartItem.setSpecification(request.getSpecification());
        }
        if (StringUtils.hasText(request.getNote())) {
            cartItem.setNote(request.getNote());
        }
        if (request.getSelected() != null) {
            cartItem.setSelected(request.getSelected());
        }
        cartItem.setUpdateTime(LocalDateTime.now());

        ShoppingCart updated = shoppingCartRepository.save(cartItem);
        return convertToDTO(updated);
    }

    @Override
    public ShoppingCartDTO updateCartItemSelected(Long cartItemId, Boolean selected) {
        ShoppingCart cartItem = shoppingCartRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("购物车项不存在"));

        cartItem.setSelected(selected);
        cartItem.setUpdateTime(LocalDateTime.now());

        ShoppingCart updated = shoppingCartRepository.save(cartItem);
        return convertToDTO(updated);
    }

    @Override
    public void removeFromCart(Long cartItemId) {
        if (!shoppingCartRepository.existsById(cartItemId)) {
            throw new RuntimeException("购物车项不存在");
        }
        shoppingCartRepository.deleteById(cartItemId);
    }

    @Override
    public void clearCart(Long userId) {
        shoppingCartRepository.deleteByUserId(userId);
    }

    @Override
    public void clearSelectedItems(Long userId) {
        shoppingCartRepository.deleteSelectedByUserId(userId);
    }

    @Override
    public Integer getCartItemCount(Long userId) {
        List<ShoppingCart> cartItems = shoppingCartRepository.findByUserId(userId);
        return cartItems.stream()
                .mapToInt(ShoppingCart::getQuantity)
                .sum();
    }

    @Override
    public BigDecimal getCartTotalAmount(Long userId) {
        List<ShoppingCart> cartItems = shoppingCartRepository.findByUserId(userId);
        return cartItems.stream()
                .filter(ShoppingCart::getSelected)
                .map(ShoppingCart::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<ShoppingCartDTO> getSelectedCartItems(Long userId) {
        List<ShoppingCart> selectedItems = shoppingCartRepository.findByUserIdAndSelected(userId, true);
        return selectedItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 购物车转为订单
    public OrderDTO checkoutFromCart(Long userId, OrderRequest orderRequest) {
        // 获取选中的购物车项
        List<ShoppingCart> selectedItems = shoppingCartRepository.findByUserIdAndSelected(userId, true);
        if (CollectionUtils.isEmpty(selectedItems)) {
            throw new RuntimeException("购物车为空");
        }

        // 构建订单请求
        OrderRequest orderReq = new OrderRequest();
        orderReq.setUserId(userId);
        orderReq.setAddress(orderRequest.getAddress());
        orderReq.setPhone(orderRequest.getPhone());
        orderReq.setNote(orderRequest.getNote());

        List<OrderRequest.OrderItemRequest> items = selectedItems.stream()
                .map(item -> {
                    OrderRequest.OrderItemRequest itemReq = new OrderRequest.OrderItemRequest();
                    itemReq.setDishId(item.getDish().getId());
                    itemReq.setQuantity(item.getQuantity());
                    return itemReq;
                })
                .collect(Collectors.toList());
        orderReq.setItems(items);

        // TODO: 调用订单服务创建订单
        // 创建订单成功后，清空购物车
        shoppingCartRepository.deleteSelectedByUserId(userId);

        return null; // 返回创建的订单
    }

    private ShoppingCartDTO convertToDTO(ShoppingCart cart) {
        ShoppingCartDTO dto = new ShoppingCartDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser() != null ? cart.getUser().getId() : null);
        dto.setUsername(cart.getUser() != null ? cart.getUser().getUsername() : null);
        dto.setDishId(cart.getDish() != null ? cart.getDish().getId() : null);
        dto.setDishName(cart.getDish() != null ? cart.getDish().getName() : null);
        dto.setDishImage(cart.getDish() != null ? cart.getDish().getImageUrl() : null);
        dto.setDishPrice(cart.getDish() != null ? cart.getDish().getPrice() : null);
        dto.setQuantity(cart.getQuantity());
        dto.setSubtotal(cart.getSubtotal());
        dto.setSpecification(cart.getSpecification());
        dto.setNote(cart.getNote());
        dto.setSelected(cart.getSelected());
        dto.setCreateTime(cart.getCreateTime());
        dto.setUpdateTime(cart.getUpdateTime());
        return dto;
    }
}