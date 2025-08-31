package com.demo.repository;

import com.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 🔍 Fetch products with stock below the defined threshold.
     */
    List<Product> findByStockLessThan(int threshold);
}