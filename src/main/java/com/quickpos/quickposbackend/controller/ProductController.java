package com.quickpos.quickposbackend.controller;

import com.quickpos.quickposbackend.model.Product;
import com.quickpos.quickposbackend.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ProductController {

    @Autowired
    private ProductRepo productRepo;

    @GetMapping()
    public ResponseEntity<List<Product>> getAllProducts(){
        try{
           List<Product> products = productRepo.findAll();
           if(products.isEmpty()) return ResponseEntity.noContent().build();
           return ResponseEntity.ok(products);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping
    public ResponseEntity<Product> getProductById(@PathVariable Long id){
        Optional<Product> productData = productRepo.findById(id);
        return productData.map(product
                -> new ResponseEntity<>(product, HttpStatus.OK)).orElseGet(()
                -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product){
        Product productData = productRepo.save(product);
        return ResponseEntity.ok(productData);
    }

    @PutMapping
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product newProductData){
        Optional<Product> productData = productRepo.findById(id);
        if(productData.isPresent()){
            Product updatedProductData = productData.get();
            updatedProductData.setCategory(newProductData.getCategory());
            updatedProductData.setName(newProductData.getName());
            updatedProductData.setPrice(newProductData.getPrice());

            Product savedProductData = productRepo.save(updatedProductData);

            return ResponseEntity.ok(savedProductData);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id){
        productRepo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
