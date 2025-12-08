package com.university.restaurant.controller;

import com.university.restaurant.dto.*;
import com.university.restaurant.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private final OrderService orderService;

    // 创建订单
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        try {
            OrderDTO order = orderService.createOrder(orderRequest);
            return ResponseEntity.ok(createSuccessResponse("订单创建成功", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 获取订单详情
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            OrderDTO order = orderService.getOrderById(id);
            return ResponseEntity.ok(createSuccessResponse("获取成功", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 根据订单号查询
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<?> getOrderByNumber(@PathVariable String orderNumber) {
        try {
            OrderDTO order = orderService.getOrderByNumber(orderNumber);
            return ResponseEntity.ok(createSuccessResponse("获取成功", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 获取所有订单
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        try {
            List<OrderDTO> orders = orderService.getAllOrders();
            return ResponseEntity.ok(createSuccessResponse("获取成功", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 获取用户订单
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUser(@PathVariable Long userId) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(createSuccessResponse("获取成功", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 分页查询订单
    @GetMapping("/page")
    public ResponseEntity<?> getOrdersByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<OrderDTO> orders = orderService.getOrdersByPage(pageable);
            return ResponseEntity.ok(createSuccessResponse("获取成功", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 更新订单状态
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            OrderDTO order = orderService.updateOrderStatus(id, status);
            String message = getStatusMessage(status);
            return ResponseEntity.ok(createSuccessResponse(message, order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 支付订单
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> payOrder(@PathVariable Long id, @RequestParam(required = false) String paymentMethod) {
        try {
            OrderDTO order = orderService.payOrder(id, paymentMethod != null ? paymentMethod : "CASH");
            return ResponseEntity.ok(createSuccessResponse("订单支付成功", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 取消订单
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, @RequestBody(required = false) Map<String, String> request) {
        try {
            String reason = request != null && request.containsKey("reason") ? request.get("reason") : "用户取消";
            OrderDTO order = orderService.cancelOrder(id, reason);
            return ResponseEntity.ok(createSuccessResponse("订单已取消", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 开始准备
    @PostMapping("/{id}/prepare")
    public ResponseEntity<?> prepareOrder(@PathVariable Long id) {
        try {
            OrderDTO order = orderService.updateOrderStatus(id, "PREPARING");
            return ResponseEntity.ok(createSuccessResponse("订单已开始准备", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 开始配送
    @PostMapping("/{id}/deliver")
    public ResponseEntity<?> deliverOrder(@PathVariable Long id) {
        try {
            OrderDTO order = orderService.deliverOrder(id);
            return ResponseEntity.ok(createSuccessResponse("订单已开始配送", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 完成订单
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeOrder(@PathVariable Long id) {
        try {
            OrderDTO order = orderService.completeOrder(id);
            return ResponseEntity.ok(createSuccessResponse("订单已完成", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 删除订单
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok(createSuccessResponse("订单删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 搜索订单
    @GetMapping("/search")
    public ResponseEntity<?> searchOrders(@RequestParam String keyword) {
        try {
            List<OrderDTO> orders = orderService.searchOrders(keyword);
            return ResponseEntity.ok(createSuccessResponse("搜索成功", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 订单统计
    @GetMapping("/stats")
    public ResponseEntity<?> getOrderStats() {
        try {
            OrderStatistics stats = orderService.getOrderStatistics();
            return ResponseEntity.ok(createSuccessResponse("获取统计成功", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<?> getUserOrderStats(@PathVariable Long userId) {
        try {
            OrderStatistics stats = orderService.getOrderStatisticsByUserId(userId);
            return ResponseEntity.ok(createSuccessResponse("获取用户统计成功", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // 订单状态流转
    private String getStatusMessage(String status) {
        return switch (status) {
            case "PENDING" -> "订单已创建";
            case "PAID" -> "订单已支付";
            case "PREPARING" -> "订单准备中";
            case "DELIVERING" -> "订单配送中";
            case "COMPLETED" -> "订单已完成";
            case "CANCELLED" -> "订单已取消";
            default -> "状态更新成功";
        };
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