package com.example.ecommerceelectroniccomponentsbackend.service;

import com.example.ecommerceelectroniccomponentsbackend.dto.CartDTO;
import com.example.ecommerceelectroniccomponentsbackend.dto.OrderDTO;
import com.example.ecommerceelectroniccomponentsbackend.entity.Cart;
import com.example.ecommerceelectroniccomponentsbackend.entity.CartItem;
import com.example.ecommerceelectroniccomponentsbackend.entity.Order;
import com.example.ecommerceelectroniccomponentsbackend.entity.OrderItem;
import com.example.ecommerceelectroniccomponentsbackend.mapper.OrderMapper;
import com.example.ecommerceelectroniccomponentsbackend.repository.CartRepository;
import com.example.ecommerceelectroniccomponentsbackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDTO createOrderFromCart(Long userId) {
        log.info("Creating order from cart for user: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create order from empty cart");
        }

        Order order = Order.builder()
                .userId(userId)
                .status(Order.OrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO)
                .build();

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productVariant(cartItem.getProductVariant())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .build();
            order.getOrderItems().add(orderItem);
            totalPrice = totalPrice.add(orderItem.getTotalPrice());
        }

        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {} for user: {}", savedOrder.getId(), userId);

        // Clear cart after order creation
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return orderMapper.toDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrderHistory(Long userId, Pageable pageable) {
        log.info("Retrieving order history for user: {}", userId);
        return orderRepository.findByUserId(userId, pageable)
                .map(orderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long orderId, Long userId) {
        log.info("Retrieving order: {} for user: {}", orderId, userId);
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found or does not belong to user"));
        return orderMapper.toDTO(order);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, Long userId, String status) {
        log.info("Updating order: {} status to: {} for user: {}", orderId, status, userId);

        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found or does not belong to user"));

        try {
            order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
            Order updatedOrder = orderRepository.save(order);
            return orderMapper.toDTO(updatedOrder);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }

    @Transactional
    public OrderDTO cancelOrder(Long orderId, Long userId) {
        log.info("Cancelling order: {} for user: {}", orderId, userId);

        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found or does not belong to user"));

        if (order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Cannot cancel delivered order");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        return orderMapper.toDTO(cancelledOrder);
    }
}
