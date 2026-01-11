package com.Shopsphere.Shopsphere.service;

import com.Shopsphere.Shopsphere.dto.requestDTO.ProductRequestDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.ProductResponseDTO;
import java.util.List;

public interface ProductService {
    ProductResponseDTO addProduct(ProductRequestDTO productRequestDTO);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO);

    void deleteProduct(Long id);

    ProductResponseDTO getProductById(Long id);

    List<ProductResponseDTO> getAllProducts();

    List<ProductResponseDTO> searchProducts(String keyword);

    List<ProductResponseDTO> getProductsByCategory(Long categoryId);
}
