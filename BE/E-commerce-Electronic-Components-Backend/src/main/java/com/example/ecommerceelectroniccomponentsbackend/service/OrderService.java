package com.example.ecommerceelectroniccomponentsbackend.service;

import com.example.ecommerceelectroniccomponentsbackend.dto.CreateOrderRequest;
import com.example.ecommerceelectroniccomponentsbackend.dto.OrderDTO;
import com.example.ecommerceelectroniccomponentsbackend.dto.SmartLockerDTO;
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
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final SmartLockerService smartLockerService;

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

        // Smart Locker integration
        String shippingMethodValue = request.getShippingMethod() != null ? request.getShippingMethod().toUpperCase() : "STANDARD";
        if ("SMART_LOCKER".equals(shippingMethodValue)) {
            if (request.getLockerId() == null) {
                throw new IllegalArgumentException("Locker ID is required for smart locker delivery");
            }

            order.setLockerId(request.getLockerId());

            // Build locker order items
            List<SmartLockerDTO.LockerOrderItem> lockerItems = cart.getCartItems().stream()
                    .map(ci -> SmartLockerDTO.LockerOrderItem.builder()
                            .name(ci.getProductVariant().getVariantName())
                            .quantity(ci.getQuantity())
                            .build())
                    .toList();

            SmartLockerDTO.LockerOrderRequest lockerRequest = SmartLockerDTO.LockerOrderRequest.builder()
                    .userId(String.valueOf(userId))
                    .lockerId(request.getLockerId())
                    .orderInfo(SmartLockerDTO.LockerOrderInfo.builder()
                            .items(lockerItems)
                            .totalPrice(finalTotal.longValue())
                            .notes(request.getNote())
                            .build())
                    .build();

            try {
                SmartLockerDTO.LockerOrderResponse lockerResponse = smartLockerService.createLockerOrder(lockerRequest);
                order.setLockerOrderId(lockerResponse.getOrderId());
                order.setCompartmentId(lockerResponse.getCompartmentId());
                order.setSenderOTP(lockerResponse.getSenderOTP());
                order.setRecipientOTP(lockerResponse.getRecipientOTP());
                log.info("Smart locker order created: {}, compartment assigned: {}",
                        lockerResponse.getOrderId(), lockerResponse.getCompartmentId());
            } catch (IllegalStateException e) {
                log.error("Failed to create smart locker order: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Failed to create smart locker order: {}", e.getMessage());
                throw new IllegalStateException("Không thể đặt tủ thông minh. Vui lòng thử lại.");
            }
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
    public Map<String, Object> getDeliveryStatusByReference(String orderReference) {
        log.info("Retrieving delivery status for reference: {}", orderReference);

        String normalizedReference = orderReference == null ? "" : orderReference.trim();
        if (normalizedReference.isEmpty()) {
            throw new IllegalArgumentException("order_id is required");
        }

        Order order = resolveOrderByReference(normalizedReference)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        String deliveryStatus = firstNonBlank(order.getDeliveryStatus(), mapInternalStatusToDeliveryStatus(order.getStatus()));
        String recipientOTP = order.getRecipientOTP();
        String senderOTP = order.getSenderOTP();
        
        String lockerOrderId = order.getLockerOrderId();
        if (lockerOrderId != null && !lockerOrderId.isBlank() && !isCompletedDeliveryStatus(deliveryStatus)) {
            try {
                // Fetch all user orders from smart locker with delivery status and OTP
                List<SmartLockerDTO.UserOrderResponse> userOrders = smartLockerService.getUserOrders(order.getUserId());
                SmartLockerDTO.UserOrderResponse matchingOrder = userOrders.stream()
                        .filter(smartOrder -> matchesAnyReference(smartOrder.getOrderId(), lockerOrderId, order.getOrderNumber(), normalizedReference))
                        .findFirst()
                        .orElse(null);
                
                if (matchingOrder != null) {
                    if (matchingOrder.getDeliveryStatus() != null && !matchingOrder.getDeliveryStatus().isBlank()) {
                        deliveryStatus = matchingOrder.getDeliveryStatus();
                    }
                    if (matchingOrder.getRecipientOTP() != null && !matchingOrder.getRecipientOTP().isBlank()) {
                        recipientOTP = matchingOrder.getRecipientOTP();
                    }
                    if (matchingOrder.getSenderOTP() != null && !matchingOrder.getSenderOTP().isBlank()) {
                        senderOTP = matchingOrder.getSenderOTP();
                    }
                    persistDeliverySync(order, deliveryStatus, recipientOTP, senderOTP);
                }
            } catch (IllegalStateException ex) {
                log.warn("Smart locker API unavailable for user {}. Fallback to internal data. Reason: {}",
                        order.getUserId(), ex.getMessage());
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("orderId", order.getId());
        response.put("orderNumber", order.getOrderNumber());
        response.put("lockerOrderId", order.getLockerOrderId());
        response.put("deliveryStatus", deliveryStatus);
        response.put("status", order.getStatus().name());
        response.put("shippingMethod", order.getShippingMethod());
        response.put("recipientOTP", recipientOTP);
        response.put("senderOTP", senderOTP);
        response.put("updatedAt", getEffectiveUpdatedAt(order));
        return response;
    }

    private java.util.Optional<Order> resolveOrderByReference(String orderReference) {
        if (orderReference.chars().allMatch(Character::isDigit)) {
            try {
                Long orderId = Long.parseLong(orderReference);
                return orderRepository.findById(orderId);
            } catch (NumberFormatException ex) {
                // Fallback to order number lookup below if parsing overflows Long.
            }
        }
        return orderRepository.findByOrderNumber(orderReference)
                .or(() -> orderRepository.findByLockerOrderId(orderReference));
    }

    private boolean matchesAnyReference(String source, String... references) {
        if (source == null || source.isBlank()) {
            return false;
        }
        String normalizedSource = source.trim();
        for (String reference : references) {
            if (reference != null && !reference.isBlank() && normalizedSource.equalsIgnoreCase(reference.trim())) {
                return true;
            }
        }
        return false;
    }

    private void persistDeliverySync(Order order, String deliveryStatus, String recipientOTP, String senderOTP) {
        boolean changed = false;

        if (!sameText(order.getDeliveryStatus(), deliveryStatus) && deliveryStatus != null && !deliveryStatus.isBlank()) {
            order.setDeliveryStatus(deliveryStatus);
            changed = true;
        }
        if (!sameText(order.getRecipientOTP(), recipientOTP) && recipientOTP != null && !recipientOTP.isBlank()) {
            order.setRecipientOTP(recipientOTP);
            changed = true;
        }
        if (!sameText(order.getSenderOTP(), senderOTP) && senderOTP != null && !senderOTP.isBlank()) {
            order.setSenderOTP(senderOTP);
            changed = true;
        }

        if (changed) {
            orderRepository.save(order);
        }
    }

    private boolean sameText(String left, String right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        return left.equals(right);
    }

    private String firstNonBlank(String preferred, String fallback) {
        if (preferred != null && !preferred.isBlank()) {
            return preferred;
        }
        return fallback;
    }

    private boolean isCompletedDeliveryStatus(String deliveryStatus) {
        if (deliveryStatus == null || deliveryStatus.isBlank()) {
            return false;
        }
        String normalized = deliveryStatus.trim().toLowerCase(Locale.ROOT);
        return "completed".equals(normalized);
    }

    private LocalDateTime getEffectiveUpdatedAt(Order order) {
        if (order.getUpdatedAt() != null) {
            return order.getUpdatedAt();
        }
        return order.getCreatedAt();
    }

    private String mapInternalStatusToDeliveryStatus(Order.OrderStatus status) {
        if (status == null) {
            return "unknown";
        }
        String mappedStatus;
        switch (status) {
            case PENDING:
                mappedStatus = "pending";
                break;
            case CONFIRMED:
                mappedStatus = "confirmed";
                break;
            case SHIPPED:
                mappedStatus = "in_transit";
                break;
            case DELIVERED:
                mappedStatus = "delivered";
                break;
            case CANCELLED:
                mappedStatus = "cancelled";
                break;
            default:
                mappedStatus = "unknown";
                break;
        }
        return mappedStatus.toLowerCase(Locale.ROOT);
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
