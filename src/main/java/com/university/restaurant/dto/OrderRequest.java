package com.university.restaurant.dto;

import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
public class OrderRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "地址不能为空")
    @Size(max = 200, message = "地址长度不能超过200个字符")
    private String address;

    @NotBlank(message = "电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String note = "";

    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "菜品ID不能为空")
        private Long dishId;

        @NotNull(message = "数量不能为空")
        @Min(value = 1, message = "数量至少为1")
        @Max(value = 100, message = "数量不能超过100")
        private Integer quantity = 1;
    }
}