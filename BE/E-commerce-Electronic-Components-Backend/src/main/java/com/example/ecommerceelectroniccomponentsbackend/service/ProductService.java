package com.example.ecommerceelectroniccomponentsbackend.service;

import com.example.ecommerceelectroniccomponentsbackend.model.Product;
import com.example.ecommerceelectroniccomponentsbackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product addProduct(Product product){
        return productRepository.saveAndFlush(product);
    }

    public List<Product> findAll(){
        List<Product> productList = new ArrayList<>();
        productRepository.findAll().forEach(productList::add);
        return productList;
    }

    public Product updateProductById(long id,
                                       Product newProduct){

        Optional<Product> existingProduct
                = productRepository.findById(id);

        if (existingProduct.isPresent()) {
            Product updatedProduct
                    = existingProduct.get();

            updatedProduct.setProductName(newProduct.getProductName());
            updatedProduct.setPrice(newProduct.getPrice());
            return productRepository.save(updatedProduct);
        }
        return null;
    }

    public void deleteProductById(long id){
        productRepository.deleteById(id);
    }
}
