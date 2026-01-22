package com.example.ecommerceelectroniccomponentsbackend.service;

import com.example.ecommerceelectroniccomponentsbackend.entity.Product;
import com.example.ecommerceelectroniccomponentsbackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product addProduct(Product product) {
        return productRepository.saveAndFlush(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product updateProductById(long id,
                                     Product newProduct) {

        Optional<Product> existingProduct
                = productRepository.findById(id);

        if (existingProduct.isPresent()) {
            Product updatedProduct
                    = existingProduct.get();

            updatedProduct.setName(newProduct.getName());
            updatedProduct.setSlug(newProduct.getSlug());
            updatedProduct.setShortDescription(newProduct.getShortDescription());
            updatedProduct.setDescription(newProduct.getDescription());
            updatedProduct.setAvgRating(newProduct.getAvgRating());
            updatedProduct.setSoldQuantity(newProduct.getSoldQuantity());
            return productRepository.save(updatedProduct);
        }
        return null;
    }

    public void deleteProductById(long id) {
        productRepository.deleteById(id);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}
