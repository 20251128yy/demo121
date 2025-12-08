package com.university.restaurant.dto;

import com.university.restaurant.entity.OrderItem;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long id;
    private Long orderId;
    private Long dishId;
    private String dishName;
    private String dishImage;
    private String category;
    private BigDecimal dishPrice;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;

    public static OrderItemDTO fromEntity(OrderItem item) {
        if (item == null) return null;

        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setOrderId(item.getOrder() != null ? item.getOrder().getId() : null);

        if (item.getDish() != null) {
            dto.setDishId(item.getDish().getId());
            dto.setDishName(item.getDish().getName());
            dto.setDishImage(item.getDish().getImageUrl());
            dto.setCategory(item.getDish().getCategory());
            dto.setDishPrice(item.getDish().getPrice());
        }

        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

        return dto;
    }
}