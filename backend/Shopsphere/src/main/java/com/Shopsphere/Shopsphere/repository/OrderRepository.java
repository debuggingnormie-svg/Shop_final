package com.Shopsphere.Shopsphere.repository;

import com.Shopsphere.Shopsphere.entity.Order;
import com.Shopsphere.Shopsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
