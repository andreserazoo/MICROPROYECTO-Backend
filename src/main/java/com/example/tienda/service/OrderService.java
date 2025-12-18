package com.example.tienda.service;

import com.example.tienda.model.Order;
import com.example.tienda.model.OrderItem;
import com.example.tienda.model.Product;
import com.example.tienda.model.User;
import com.example.tienda.repository.OrderRepository;
import com.example.tienda.repository.ProductRepository;
import com.example.tienda.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Order> findOrdersByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUser(user);
    }

    @Transactional
    public Order createOrder(String username, List<OrderItem> items) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // Update stock
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            // Set item details
            item.setProduct(product);
            item.setPrice(product.getPrice());
            item.setOrder(order);

            total = total.add(product.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        order.setItems(items);
        order.setTotal(total);

        return orderRepository.save(order);
    }
}
