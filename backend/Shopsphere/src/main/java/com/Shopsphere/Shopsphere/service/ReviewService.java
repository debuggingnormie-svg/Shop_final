package com.Shopsphere.Shopsphere.service;

import com.Shopsphere.Shopsphere.dto.requestDTO.ReviewRequestDTO;
import com.Shopsphere.Shopsphere.dto.responseDTO.ReviewResponseDTO;
import java.util.List;

public interface ReviewService {
    ReviewResponseDTO addReview(ReviewRequestDTO reviewRequestDTO);

    List<ReviewResponseDTO> getReviewsByProductId(Long productId);
}
