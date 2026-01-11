package com.Shopsphere.Shopsphere.dto.responseDTO;

import lombok.Data;

@Data
public class ReviewResponseDTO {
    private Long id;
    private String userName;
    private Integer rating;
    private String comment;
}
