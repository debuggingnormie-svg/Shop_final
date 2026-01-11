package com.Shopsphere.Shopsphere.repository;

import com.Shopsphere.Shopsphere.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Shopsphere.Shopsphere.entity.Cart;
import com.Shopsphere.Shopsphere.entity.Product;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    void deleteByCart(Cart cart);
}
