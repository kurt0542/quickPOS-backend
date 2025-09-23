package com.quickpos.quickposbackend.model;

import com.quickpos.quickposbackend.model.enums.ProductCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    private String name;
    private Double price;
    private String imageUrl;

    private String allergen;

    private Integer salesCount = 0;

}
