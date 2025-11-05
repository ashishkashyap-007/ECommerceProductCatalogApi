package com.ashish.ecommerce.service;

import com.ashish.ecommerce.model.Product;
import com.ashish.ecommerce.repository.ProductRepository;
import com.ashish.ecommerce.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Page<Product> getProducts(
            String category,
            String search,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            Sort sort
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);
        return productRepository.findAll(ProductSpecification.filter(category, search, minPrice, maxPrice), pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product create(Product product) {
        product.setId(null);
        return productRepository.save(product);
    }

    @Transactional
    public Optional<Product> update(Long id, Product updatedProduct) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(updatedProduct.getName());
            existing.setDescription(updatedProduct.getDescription());
            existing.setPrice(updatedProduct.getPrice());
            existing.setCategory(updatedProduct.getCategory());
            existing.setUpdatedAt(OffsetDateTime.now());
            return productRepository.save(existing);
        });
    }

    @Transactional
    public boolean delete(Long id) {
        return productRepository.findById(id).map(product -> {
            productRepository.deleteById(id);
            return true;
        }).orElse(false);
    }
}


