package com.Shopsphere.Shopsphere.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // title -> name

    private Double price;
    private String description;
    private String imageUrl;

    // New fields
    private Integer stockLevel;
    private Boolean isActive;
    private Integer reorderThreshold;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
