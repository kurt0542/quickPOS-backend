package com.quickpos.quickposbackend.service;

import com.quickpos.quickposbackend.model.Product;
import com.quickpos.quickposbackend.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

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
                    existing.setSalesCount(newProductData.getSalesCount());
                    return productRepo.save(existing);
                }).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void deleteProduct(Long id) {
        productRepo.deleteById(id);
    }
}
