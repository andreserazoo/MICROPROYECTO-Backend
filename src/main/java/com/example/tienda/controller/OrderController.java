package com.example.tienda.controller;

import com.example.tienda.model.Order;
import com.example.tienda.model.OrderItem;
import com.example.tienda.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<Order> getMyOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return orderService.findOrdersByUser(auth.getName());
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody List<OrderItem> items) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Order order = orderService.createOrder(auth.getName(), items);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
