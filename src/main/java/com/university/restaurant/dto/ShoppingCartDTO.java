package com.university.restaurant.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShoppingCartDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long dishId;
    private String dishName;
    private String dishImage;
    private BigDecimal dishPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private String specification;
    private String note;
    private Boolean selected;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}