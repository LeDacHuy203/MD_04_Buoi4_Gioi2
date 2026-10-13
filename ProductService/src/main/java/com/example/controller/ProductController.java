package com.example.controller;

import com.example.dto.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        // Dữ liệu mẫu khớp với yêu cầu đề bài
        Product product = new Product(id, "IPhone 17", 123456.0, 50);
        return ResponseEntity.ok(product);
    }
}
