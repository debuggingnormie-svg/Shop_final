package com.Shopsphere.Shopsphere.service.impl;

import com.Shopsphere.Shopsphere.dto.requestDTO.ProductRequestDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.ProductResponseDTO;
import com.Shopsphere.Shopsphere.entity.Product;
import com.Shopsphere.Shopsphere.entity.Category;
import com.Shopsphere.Shopsphere.exception.ResourceNotFoundException;
import com.Shopsphere.Shopsphere.repository.ProductRepository;
import com.Shopsphere.Shopsphere.repository.CategoryRepository;
import com.Shopsphere.Shopsphere.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ProductResponseDTO addProduct(ProductRequestDTO productRequestDTO) {
        Category category;
        if (productRequestDTO.getCategoryId() != null) {
            category = categoryRepository.findById(productRequestDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with ID: " + productRequestDTO.getCategoryId()));
        } else if (productRequestDTO.getCategoryName() != null) {
            category = categoryRepository.findByName(productRequestDTO.getCategoryName())
                    .orElseGet(() -> {
                        Category newCat = new Category();
                        newCat.setName(productRequestDTO.getCategoryName());
                        return categoryRepository.save(newCat);
                    });
        } else {
            throw new IllegalArgumentException("Category ID or Name must be provided");
        }

        Product product = new Product();
        product.setName(productRequestDTO.getName());
        product.setPrice(productRequestDTO.getPrice());
        product.setDescription(productRequestDTO.getDescription());
        product.setImageUrl(productRequestDTO.getImageUrl());
        product.setCategory(category);

        // New fields
        product.setStockLevel(productRequestDTO.getStockLevel() != null ? productRequestDTO.getStockLevel() : 0);
        product.setIsActive(productRequestDTO.getIsActive() != null ? productRequestDTO.getIsActive() : true);
        product.setReorderThreshold(
                productRequestDTO.getReorderThreshold() != null ? productRequestDTO.getReorderThreshold() : 0);

        Product savedProduct = productRepository.save(product);
        return mapToDTO(savedProduct);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Category category;
        if (productRequestDTO.getCategoryId() != null) {
            category = categoryRepository.findById(productRequestDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with ID: " + productRequestDTO.getCategoryId()));
        } else if (productRequestDTO.getCategoryName() != null) {
            category = categoryRepository.findByName(productRequestDTO.getCategoryName())
                    .orElseGet(() -> {
                        Category newCat = new Category();
                        newCat.setName(productRequestDTO.getCategoryName());
                        return categoryRepository.save(newCat);
                    });
        } else {
            // Keep existing category if nothing provided? Or throw?
            // Assuming strict validation for now based on previous logic
            category = product.getCategory(); // Fallback to existing? Or throw.
            // Let's keep it safe: if null, keep existing.
            if (category == null)
                throw new IllegalArgumentException("Product must have a category");
        }

        product.setName(productRequestDTO.getName());
        product.setPrice(productRequestDTO.getPrice());
        product.setDescription(productRequestDTO.getDescription());
        product.setImageUrl(productRequestDTO.getImageUrl());
        if (category != null)
            product.setCategory(category);

        if (productRequestDTO.getStockLevel() != null)
            product.setStockLevel(productRequestDTO.getStockLevel());
        if (productRequestDTO.getIsActive() != null)
            product.setIsActive(productRequestDTO.getIsActive());
        if (productRequestDTO.getReorderThreshold() != null)
            product.setReorderThreshold(productRequestDTO.getReorderThreshold());

        Product updatedProduct = productRepository.save(product);
        return mapToDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return mapToDTO(product);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDTO> searchProducts(String keyword) {
        return getAllProducts().stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDTO> getProductsByCategory(Long categoryId) {
        return getAllProducts().stream()
                .filter(p -> p.getCategoryName().equals(categoryRepository.findById(categoryId).get().getName()))
                .collect(Collectors.toList());
    }

    private ProductResponseDTO mapToDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setImageUrl(product.getImageUrl());
        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
        }
        dto.setStockLevel(product.getStockLevel());
        dto.setIsActive(product.getIsActive());
        dto.setReorderThreshold(product.getReorderThreshold());
        return dto;
    }
}
