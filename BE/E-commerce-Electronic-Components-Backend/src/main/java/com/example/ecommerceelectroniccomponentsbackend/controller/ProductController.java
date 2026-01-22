package com.example.ecommerceelectroniccomponentsbackend.controller;

import com.example.ecommerceelectroniccomponentsbackend.entity.Product;
import com.example.ecommerceelectroniccomponentsbackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    // GET API → Fetch all details
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    // POST API → Add new detail
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product addProduct(@RequestBody Product products) {
        return productService.addProduct(products);
    }

    // PUT API → Update detail by ID
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Product updateDetails(@PathVariable int id, @RequestBody Product updatedProduct) {
        return productService.updateProductById(id, updatedProduct);
    }

    // DELETE API → Remove detail by ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteDetails(@PathVariable int id) {
        productService.deleteProductById(id);
        return "Product with id: " + id + " has been deleted";
    }
}