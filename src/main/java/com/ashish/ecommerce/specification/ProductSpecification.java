package com.ashish.ecommerce.specification;

import com.ashish.ecommerce.model.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> filter(
            String category,
            String search,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (category != null && !category.isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(criteriaBuilder.lower(root.get("category")), category.toLowerCase()));
            }

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                Predicate nameLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern);
                Predicate descLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(nameLike, descLike));
            }

            if (minPrice != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.ge(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.le(root.get("price"), maxPrice));
            }
            return predicate;
        };
    }
}
