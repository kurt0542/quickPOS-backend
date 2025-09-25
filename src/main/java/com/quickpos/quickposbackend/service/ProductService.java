package com.quickpos.quickposbackend.service;

import com.quickpos.quickposbackend.model.Product;
import com.quickpos.quickposbackend.repository.ProductRepo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepo productRepo;
    private final ImageService imageService;

    public ProductService(ProductRepo productRepo, ImageService imageService) {
        this.productRepo = productRepo;
        this.imageService = imageService;
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepo.findById(id);
    }

    public Product addProduct(Product product) {
        return productRepo.save(product);
    }

    public Product updateProduct(Long id, Product newProductData) {
        return productRepo.findById(id)
                .map(existing -> {
                    existing.setName(newProductData.getName());
                    existing.setCategory(newProductData.getCategory());
                    existing.setPrice(newProductData.getPrice());
                    existing.setImageUrl(newProductData.getImageUrl());
                    existing.setAllergen(newProductData.getAllergen());
                    existing.setSalesCount(newProductData.getSalesCount());
                    return productRepo.save(existing);
                }).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void deleteProduct(Long id) {
        productRepo.findById(id).ifPresent(product -> {
            try {
                imageService.deleteImage(product.getImageUrl());
            } catch (IOException e) {
                System.err.println("Failed to delete image: " + e.getMessage());
            }
            productRepo.delete(product);
        });
    }
}
