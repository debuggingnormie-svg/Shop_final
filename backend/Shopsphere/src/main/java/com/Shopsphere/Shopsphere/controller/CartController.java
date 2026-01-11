package com.Shopsphere.Shopsphere.controller;

import com.Shopsphere.Shopsphere.dto.requestDTO.CartItemRequestDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.CartResponseDTO;
import com.Shopsphere.Shopsphere.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @PostMapping
    public ResponseEntity<CartResponseDTO> addToCart(@RequestBody CartItemRequestDTO cartItemRequestDTO) {
        return ResponseEntity.ok(cartService.addToCart(cartItemRequestDTO));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<CartResponseDTO> removeFromCart(@PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeFromCart(productId));
    }

    @PutMapping
    public ResponseEntity<CartResponseDTO> updateCartItemQuantity(@RequestBody CartItemRequestDTO cartItemRequestDTO) {
        return ResponseEntity.ok(cartService.updateCartItemQuantity(cartItemRequestDTO));
    }
}
