package com.quickpos.quickposbackend.service;

import com.quickpos.quickposbackend.model.Product;
import com.quickpos.quickposbackend.repository.ProductRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public Product updateProduct(Long id, Product newProductData, MultipartFile file) {
        return productRepo.findById(id)
                .map(existing -> {
                    existing.setName(newProductData.getName());
                    existing.setPrice(newProductData.getPrice());
                    existing.setAllergen(newProductData.getAllergen());
                    existing.setSalesCount(newProductData.getSalesCount());
                    existing.setHasSugarOption(newProductData.isHasSugarOption());
                    existing.setHasIceOption(newProductData.isHasIceOption());
                    existing.setHasCupSizeOption(newProductData.isHasCupSizeOption());

                    if (file != null && !file.isEmpty()) {
                        try {
                            if (existing.getImageUrl() != null) {
                                imageService.deleteImage(existing.getImageUrl());
                            }

                            String newImageUrl = imageService.saveImage(file);
                            existing.setImageUrl(newImageUrl);

                        } catch (IOException e) {
                            throw new RuntimeException("Failed to update product image", e);
                        }
                    } else {
                        existing.setImageUrl(newProductData.getImageUrl());
                    }

                    return productRepo.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
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
