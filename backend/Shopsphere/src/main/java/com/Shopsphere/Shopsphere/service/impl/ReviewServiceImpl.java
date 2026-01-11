package com.Shopsphere.Shopsphere.service.impl;

import com.Shopsphere.Shopsphere.dto.requestDTO.ReviewRequestDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.ReviewResponseDTO;
import com.Shopsphere.Shopsphere.entity.Product;
import com.Shopsphere.Shopsphere.entity.Review;
import com.Shopsphere.Shopsphere.entity.User;
import com.Shopsphere.Shopsphere.exception.ResourceNotFoundException;
import com.Shopsphere.Shopsphere.exception.UserNotFoundException;
import com.Shopsphere.Shopsphere.repository.ProductRepository;
import com.Shopsphere.Shopsphere.repository.ReviewRepository;
import com.Shopsphere.Shopsphere.repository.UserRepository;
import com.Shopsphere.Shopsphere.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

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
    public ReviewResponseDTO addReview(ReviewRequestDTO reviewRequestDTO) {
        User user = getAuthenticatedUser();
        Product product = productRepository.findById(reviewRequestDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(reviewRequestDTO.getRating());
        review.setComment(reviewRequestDTO.getComment());

        Review savedReview = reviewRepository.save(review);
        return mapToDTO(savedReview);
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ReviewResponseDTO mapToDTO(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setUserName(review.getUser().getName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        return dto;
    }
}
