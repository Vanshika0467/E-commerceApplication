package com.demo.service;

import com.demo.entity.Product;
import com.demo.exception.CustomException;
import com.demo.repository.OrderItemRepository;
import com.demo.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional
    public Product createProduct(Product product) {
        if (product.getName() == null || product.getPrice() == null) {
            throw new CustomException("INVALID_DATA", "Product name and price must not be null", product.getId());
        }

        logger.info("Creating product: {}", product);
        Product savedProduct = productRepository.save(product);

        // ✅ Sync product quantity into Inventory
        inventoryService.initializeInventory(savedProduct, product.getStock());

        logger.info("Initialized inventory for product '{}' with quantity {}", savedProduct.getName(), product.getStock());
        return savedProduct;
    }

    public List<Product> getAllProducts() {
        logger.info("Fetching all products");
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        logger.info("Fetching product with ID: {}", id);
        return productRepository.findById(id)
            .orElseThrow(() -> new CustomException("PRODUCT_NOT_FOUND", "Product not found", id));
    }

    @Transactional
    public Product deleteProductById(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));

        // ✅ Check if product is referenced in OrderItems
        boolean isReferenced = orderItemRepository.existsByProduct_Id(productId);
        if (isReferenced) {
            throw new IllegalStateException("Cannot delete product: it is still referenced in order items.");
        }

        // ✅ Unlink from Inventory via service
        inventoryService.unlinkProductFromInventory(product);

        // ✅ Delete product safely
        productRepository.delete(product);
        logger.info("Deleted product with ID: {}", productId);

        return product;
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        logger.info("Updating product with ID: {}", id);
        Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new CustomException("PRODUCT_NOT_FOUND", "Product not found", id));

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setStock(updatedProduct.getStock());

        return productRepository.save(existingProduct);
    }

    public Product updateProductName(Long id, String newName) {
        logger.info("Updating product name for ID: {}", id);
        Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new CustomException("PRODUCT_NOT_FOUND", "Product not found", id));

        existingProduct.setName(newName);
        return productRepository.save(existingProduct);
    }
}