package com.university.restaurant.dto;

import com.university.restaurant.entity.Order;
import com.university.restaurant.entity.OrderItem;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String username;
    private String userPhone;
    private BigDecimal totalAmount;
    private String status;
    private String address;
    private String phone;
    private String note;
    private LocalDateTime orderTime;
    private LocalDateTime paidTime;
    private LocalDateTime preparingTime;
    private LocalDateTime deliveringTime;
    private LocalDateTime completeTime;
    private LocalDateTime cancelTime;
    private List<OrderItemDTO> items;

    public static OrderDTO fromEntity(Order order) {
        if (order == null) return null;

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());

        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setUsername(order.getUser().getUsername());
            dto.setUserPhone(order.getUser().getPhone());
        }

        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setAddress(order.getAddress());
        dto.setPhone(order.getPhone());
        dto.setNote(order.getNote());
        dto.setOrderTime(order.getOrderTime());
        dto.setPaidTime(order.getPaidTime());
        dto.setPreparingTime(order.getPreparingTime());
        dto.setDeliveringTime(order.getDeliveringTime());
        dto.setCompleteTime(order.getCompleteTime());
        dto.setCancelTime(order.getCancelTime());

        if (order.getOrderItems() != null) {
            dto.setItems(order.getOrderItems().stream()
                    .map(OrderItemDTO::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}