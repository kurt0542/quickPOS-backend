package com.quickpos.quickposbackend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickpos.quickposbackend.model.Product;
import com.quickpos.quickposbackend.model.enums.ProductCategory;
import com.quickpos.quickposbackend.service.ImageService;
import com.quickpos.quickposbackend.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ImageService imageService;
    private final ObjectMapper objectMapper;
    @GetMapping("/getAllProducts")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/getProductById/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(
           @RequestParam("product") String productJson,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {

        JsonNode node = objectMapper.readTree(productJson);
        Product product = new Product();
        product.setName(node.get("name").asText());
        product.setCategory(ProductCategory.valueOf(node.get("category").asText()));
        product.setPrice(node.get("price").asDouble());
        product.setAllergen(node.get("allergen").asText());
        product.setSalesCount(0);
        product.setHasSugarOption(node.get("hasSugarOption").asBoolean(false));
        product.setHasIceOption(node.get("hasIceOption").asBoolean(false));
        product.setHasCupSizeOption(node.get("hasCupSizeOption").asBoolean(false));
        product.setId(null);

        if (file != null && !file.isEmpty()) {
            String imageUrl = imageService.saveImage(file);
            product.setImageUrl(imageUrl);
        }

        Product savedProduct = productService.addProduct(product);

        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
                                                 @RequestBody Product product) {
        try {
            return ResponseEntity.ok(productService.updateProduct(id, product));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
