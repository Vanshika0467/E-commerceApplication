package com.demo.service;

import com.demo.entity.Cart;
import com.demo.entity.CartItem;
import com.demo.entity.Product;
import com.demo.exception.CustomException;
import com.demo.repository.CartItemRepository;
import com.demo.repository.CartRepository;
import com.demo.repository.ProductRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    // ✅ Get cart by ID
    public Cart getCart(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new CustomException("Cart not found", "with id", cartId));
    }

    // ✅ Get cart by user ID
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("Cart not found", "for user", userId));
    }

    // ✅ Get all items in a user's cart
    public List<CartItem> getItemsForUser(Long userId) {
        Cart cart = getCartByUserId(userId);
        return cart.getItems();
    }

    // ✅ Add product to cart
    public Cart addItemToCart(Long cartId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new CustomException("Invalid quantity", "Must be greater than zero", quantity);
        }

        Cart cart = getCart(cartId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException("Product not found", "with id", productId));

        if (product.getStock() < quantity) {
            throw new CustomException("Insufficient stock", "Available stock is", product.getStock());
        }

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            log.info("Updated quantity of product {} in cart {}", productId, cartId);
        } else {
            CartItem newItem = new CartItem(product, quantity, product.getPrice(), cart);
            cart.getItems().add(newItem);
            log.info("Added new product {} to cart {}", productId, cartId);
        }

        return cartRepository.save(cart);
    }

    // ✅ Remove item from cart
    public Cart removeItemFromCart(Long cartId, Long itemId) {
        Cart cart = getCart(cartId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException("Cart item not found", "with id", itemId));

        if (!item.getCart().getId().equals(cartId)) {
            throw new IllegalArgumentException("Item does not belong to this cart");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        log.info("Removed item {} from cart {}", itemId, cartId);

        return cartRepository.save(cart);
    }

    // ✅ Update item quantity
    public Cart updateItemQuantity(Long cartId, Long itemId, int quantity) {
        if (quantity <= 0) {
            throw new CustomException("Invalid quantity", "Must be greater than zero", quantity);
        }

        Cart cart = getCart(cartId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException("Cart item not found", "with id", itemId));

        if (!item.getCart().getId().equals(cartId)) {
            throw new IllegalArgumentException("Item does not belong to this cart");
        }

        item.setQuantity(quantity);
        log.info("Updated quantity of item {} in cart {} to {}", itemId, cartId, quantity);

        return cartRepository.save(cart);
    }

    // ✅ Clear cart by cart ID
    public Cart clearCart(Long cartId) {
        Cart cart = getCart(cartId);
        cart.getItems().forEach(cartItemRepository::delete);
        cart.getItems().clear();
        log.info("Cleared all items from cart {}", cartId);
        return cartRepository.save(cart);
    }

    // ✅ Clear cart by user ID
    public void clearCartByUserId(Long userId) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().forEach(cartItemRepository::delete);
        cart.getItems().clear();
        cartRepository.save(cart);
        log.info("Cleared cart for user {}", userId);
    }
}