package com.Shopsphere.Shopsphere.service;

import com.Shopsphere.Shopsphere.dto.requestDTO.OrderRequestDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.OrderResponseDTO;
import java.util.List;

public interface OrderService {
    OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO);

    List<OrderResponseDTO> getMyOrders();

    OrderResponseDTO getOrderById(Long id);

    List<OrderResponseDTO> getAllOrders(); // For Order

    OrderResponseDTO updateOrderStatus(Long id, String status);
}
