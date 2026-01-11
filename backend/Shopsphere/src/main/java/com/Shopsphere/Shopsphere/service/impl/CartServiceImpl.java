package com.Shopsphere.Shopsphere.service.impl;

import com.Shopsphere.Shopsphere.dto.requestDTO.CartItemRequestDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.ProductResponseDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.CartItemResponseDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.CartResponseDTO;
import com.Shopsphere.Shopsphere.entity.Product;
import com.Shopsphere.Shopsphere.entity.Cart;
import com.Shopsphere.Shopsphere.entity.CartItem;
import com.Shopsphere.Shopsphere.entity.User;
import com.Shopsphere.Shopsphere.exception.ResourceNotFoundException;
import com.Shopsphere.Shopsphere.exception.UserNotFoundException;
import com.Shopsphere.Shopsphere.repository.ProductRepository;
import com.Shopsphere.Shopsphere.repository.CartItemRepository;
import com.Shopsphere.Shopsphere.repository.CartRepository;
import com.Shopsphere.Shopsphere.repository.UserRepository;
import com.Shopsphere.Shopsphere.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public CartResponseDTO getCart() {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
        return mapToDTO(cart);
    }

    @Override
    public CartResponseDTO addToCart(CartItemRequestDTO cartItemRequestDTO) {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(cartItemRequestDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + cartItemRequestDTO.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(cartItemRequestDTO.getQuantity());
            cartItemRepository.save(newItem);
        }

        return getCart();
    }

    @Override
    public CartResponseDTO removeFromCart(Long productId) {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        cartItemRepository.delete(item);
        return getCart();
    }

    @Override
    public CartResponseDTO updateCartItemQuantity(CartItemRequestDTO cartItemRequestDTO) {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        Product product = productRepository.findById(cartItemRequestDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        item.setQuantity(cartItemRequestDTO.getQuantity());
        cartItemRepository.save(item);
        return getCart();
    }

    @Override
    @Transactional
    public void clearCart() {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findByUser(user).orElse(null);
        if (cart != null) {
            cartItemRepository.deleteByCart(cart);
        }
    }

    private CartResponseDTO mapToDTO(Cart cart) {
        CartResponseDTO dto = new CartResponseDTO();
        dto.setId(cart.getId());

        List<CartItem> items = cartItemRepository.findAll().stream()
                .filter(i -> i.getCart().getId().equals(cart.getId()))
                .collect(Collectors.toList());

        List<CartItemResponseDTO> itemDTOs = items.stream().map(item -> {
            CartItemResponseDTO itemDTO = new CartItemResponseDTO();
            itemDTO.setId(item.getId());
            itemDTO.setQuantity(item.getQuantity());

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

        double total = items.stream().mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity()).sum();
        dto.setTotalAmount(total);

        return dto;
    }
}
