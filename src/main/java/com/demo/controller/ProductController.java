package com.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.apierror.ApiError;
import com.demo.entity.Product;
import com.demo.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    // ✅ Create a product
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        logger.info("Creating product: {}", product.getName());
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // ✅ Get all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        logger.info("Fetching all products");
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // ✅ Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        logger.info("Fetching product with ID: {}", id);
        Product product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    // ✅ Delete product safely
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long id) {
        try {
            Product deletedProduct = productService.deleteProductById(id);
            return ResponseEntity.ok(deletedProduct);
        } catch (IllegalStateException e) {
            ApiError error = new ApiError(
                409,
                "Conflict",
                "PRODUCT_DELETION_CONSTRAINT",
                "Cannot delete product with ID " + id + " because it is still referenced in cart items.",
                "To safely delete this product, unlink or remove dependent cart items first.",
                LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }

    // ✅ Update product by ID
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        logger.info("Updating product with ID: {}", id);
        Product product = productService.updateProduct(id, updatedProduct);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    // ✅ Update product name only
    @PatchMapping("/{id}/name")
    public ResponseEntity<Product> updateProductName(@PathVariable Long id, @RequestBody String newName) {
        String cleanedName = newName.replaceAll("^\"|\"$", "");
        logger.info("Updating product name for ID {} to '{}'", id, cleanedName);
        Product updatedProduct = productService.updateProductName(id, cleanedName);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
}