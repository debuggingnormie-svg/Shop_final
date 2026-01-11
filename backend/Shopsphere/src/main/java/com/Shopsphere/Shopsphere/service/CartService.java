package com.Shopsphere.Shopsphere.service;

import com.Shopsphere.Shopsphere.dto.requestDTO.CartItemRequestDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.CartResponseDTO;

public interface CartService {
    CartResponseDTO getCart();

    CartResponseDTO addToCart(CartItemRequestDTO cartItemRequestDTO);

    CartResponseDTO removeFromCart(Long productId);

    CartResponseDTO updateCartItemQuantity(CartItemRequestDTO cartItemRequestDTO);

    void clearCart();
}
