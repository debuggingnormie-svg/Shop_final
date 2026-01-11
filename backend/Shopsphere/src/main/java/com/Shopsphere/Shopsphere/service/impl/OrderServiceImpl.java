package com.Shopsphere.Shopsphere.service.impl;

import com.Shopsphere.Shopsphere.dto.requestDTO.OrderRequestDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.ProductResponseDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.OrderItemResponseDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.OrderResponseDTO;
import com.Shopsphere.Shopsphere.entity.*;
import com.Shopsphere.Shopsphere.exception.ResourceNotFoundException;
import com.Shopsphere.Shopsphere.exception.UserNotFoundException;
import com.Shopsphere.Shopsphere.repository.*;
import com.Shopsphere.Shopsphere.service.CartService;
import com.Shopsphere.Shopsphere.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService; // To clear cart

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO) {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart is empty"));

        List<CartItem> cartItems = cartItemRepository.findAll().stream()
                .filter(i -> i.getCart().getId().equals(cart.getId()))
                .collect(Collectors.toList());

        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty");
        }

        ShippingAddress address = shippingAddressRepository.findById(orderRequestDTO.getShippingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Shipping Address not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setShippingAddress(address);

        double total = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStockLevel() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
            }
            product.setStockLevel(product.getStockLevel() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());

            total += product.getPrice() * cartItem.getQuantity();

            orderItemRepository.save(orderItem);
            orderItems.add(orderItem);
        }

        savedOrder.setTotalAmount(total);
        orderRepository.save(savedOrder); // Update total

        cartService.clearCart();

        return mapToDTO(savedOrder, orderItems);
    }

    @Override
    public List<OrderResponseDTO> getMyOrders() {
        User user = getAuthenticatedUser();
        return orderRepository.findByUser(user).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return mapToDTO(order);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
        return mapToDTO(order);
    }

    private OrderResponseDTO mapToDTO(Order order) {
        List<OrderItem> items = orderItemRepository.findAll().stream()
                .filter(i -> i.getOrder().getId().equals(order.getId()))
                .collect(Collectors.toList());
        return mapToDTO(order, items);
    }

    private OrderResponseDTO mapToDTO(Order order, List<OrderItem> items) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());

        List<OrderItemResponseDTO> itemDTOs = items.stream().map(item -> {
            OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
            itemDTO.setId(item.getId());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPrice(item.getPrice());

            ProductResponseDTO productDTO = new ProductResponseDTO();
            productDTO.setId(item.getProduct().getId());
            productDTO.setName(item.getProduct().getName());
            productDTO.setPrice(item.getProduct().getPrice());
            productDTO.setDescription(item.getProduct().getDescription());
            productDTO.setImageUrl(item.getProduct().getImageUrl());
            if (item.getProduct().getCategory() != null) {
                productDTO.setCategoryName(item.getProduct().getCategory().getName());
            }
            productDTO.setStockLevel(item.getProduct().getStockLevel());
            productDTO.setIsActive(item.getProduct().getIsActive());
            productDTO.setReorderThreshold(item.getProduct().getReorderThreshold());

            itemDTO.setProduct(productDTO);

            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }
}
