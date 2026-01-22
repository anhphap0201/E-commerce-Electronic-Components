package com.example.ecommerceelectroniccomponentsbackend.repository;

import com.example.ecommerceelectroniccomponentsbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);

    // Tìm sản phẩm theo tên danh mục
    @Query("select distinct p from Product p join p.categories c where LOWER(c.name) like lower(concat('%', :categoryName, '%') ) ")
    List<Product> findByCategoryNameContaining(@Param("categoryName") String categoryName);

    // Tìm sản phẩm theo ID danh mục
    @Query("select distinct p from Product p join p.categories c where c.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);

    // Tìm sản phẩm theo slug danh mục (exact match, case insensitive)
    @Query("select distinct p from Product p join p.categories c where lower(c.slug) = lower(:categorySlug) ")
    List<Product> findByCategorySlug(@Param("categorySlug") String categorySlug);

    // Tìm sản phẩm theo slug danh mục (partial match, case insensitive) - thêm method mới
    @Query("select distinct p from Product p join p.categories c where lower(c.slug) like lower(concat('%', :categorySlug, '%') ) ")
    List<Product> findByCategorySlugContaining(@Param("categorySlug") String categorySlug);
}



