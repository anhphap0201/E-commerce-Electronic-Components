package com.example.ecommerceelectroniccomponentsbackend.service;

import com.example.ecommerceelectroniccomponentsbackend.dto.CreateOrderRequest;
import com.example.ecommerceelectroniccomponentsbackend.dto.OrderDTO;
import com.example.ecommerceelectroniccomponentsbackend.entity.Cart;
import com.example.ecommerceelectroniccomponentsbackend.entity.CartItem;
import com.example.ecommerceelectroniccomponentsbackend.entity.Order;
import com.example.ecommerceelectroniccomponentsbackend.entity.OrderItem;
import com.example.ecommerceelectroniccomponentsbackend.model.User;
import com.example.ecommerceelectroniccomponentsbackend.mapper.OrderMapper;
import com.example.ecommerceelectroniccomponentsbackend.repository.CartRepository;
import com.example.ecommerceelectroniccomponentsbackend.repository.OrderRepository;
import com.example.ecommerceelectroniccomponentsbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDTO createOrderFromCart(Long userId, CreateOrderRequest request) {
        log.info("Creating order from cart for user: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create order from empty cart");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate user profile
        validateUserProfile(user);

        // Build address and phone from user profile
        String shippingAddress = buildShippingAddress(user);
        String phone = safeTrim(user.getPhone());
        String province = safeTrim(user.getProvince());
        String ward = safeTrim(user.getWard());

        // Calculate totals
        BigDecimal itemsSubtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getCartItems()) {
            itemsSubtotal = itemsSubtotal.add(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        BigDecimal shippingFee = request.getShippingFee() != null ? request.getShippingFee() : BigDecimal.ZERO;
        BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;
        BigDecimal finalTotal = itemsSubtotal.add(shippingFee).subtract(discount);

        // Generate unique order number
        String orderNumber = "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Build order with all required fields
        Order order = Order.builder()
                .userId(userId)
                .status(Order.OrderStatus.PENDING)
                // Money fields - populate all with calculated values (schema requires all NOT NULL)
                .totalCost(finalTotal)
                .totalPrice(finalTotal)
                .subtotal(itemsSubtotal)
                .total(finalTotal)
                .shippingFee(shippingFee)
                .shippingCost(shippingFee)
                .discount(discount)
                // Customer info
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(phone)
                // Address
                .address(shippingAddress)
                .province(province)
                .provinceCode("") // User model doesn't have province code
                .ward(ward)
                .wardCode("") // User model doesn't have ward code
                // Order details
                .orderNumber(orderNumber)
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "COD")
                .paymentStatus("PENDING")
                .shippingMethod(request.getShippingMethod() != null ? request.getShippingMethod().toUpperCase() : "STANDARD")
                .note(request.getNote())
                .voucherCode(request.getVoucherCode())
                .build();

        // Add order items
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productVariant(cartItem.getProductVariant())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .build();
            order.getOrderItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {} and number: {} for user: {}", savedOrder.getId(), savedOrder.getOrderNumber(), userId);

        // Clear cart after order creation
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return orderMapper.toDTO(savedOrder);
    }

    private void validateUserProfile(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required. Please update your profile.");
        }
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required. Please update your profile.");
        }
        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required. Please update your profile.");
        }
        if (user.getDetailedAddress() == null || user.getDetailedAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Detailed address is required. Please update your profile.");
        }
        if (user.getWard() == null || user.getWard().trim().isEmpty()) {
            throw new IllegalArgumentException("Ward is required. Please update your profile.");
        }
        if (user.getProvince() == null || user.getProvince().trim().isEmpty()) {
            throw new IllegalArgumentException("Province is required. Please update your profile.");
        }
    }

    private String buildShippingAddress(User user) {
        String detailedAddress = safeTrim(user.getDetailedAddress());
        String ward = safeTrim(user.getWard());
        String province = safeTrim(user.getProvince());
        return String.join(", ", detailedAddress, ward, province);
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
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
