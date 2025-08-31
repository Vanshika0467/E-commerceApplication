package com.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.demo.entity.Cart;
import com.demo.entity.OtpToken;
import com.demo.entity.User;
import com.demo.repository.CartRepository;
import com.demo.repository.OtpTokenRepository;
import com.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;
    
   
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpTokenRepository otpTokenRepository;

    // ✅ Register new user with OTP verification and auto-linked cart
    public User registerUser(User user, String otp) {
        // Check for duplicate email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Verify OTP before proceeding
        boolean verified = verifyOtp(user.getEmail(), otp);
        if (!verified) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        // Mark email as verified
        user.setEmailVerified(true);

        // Create and link cart before saving
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);

        // Save user (cascade will save cart)
        return userRepository.save(user);
    }

    // ✅ Get user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // ✅ Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ Update user by ID
    public User updateUser(Long id, User updatedUser) {
        User existingUser = getUserById(id);

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());

        return userRepository.save(existingUser);
    }

    // ✅ Delete user by ID
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // ✅ Find user by email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
    public void sendOtp(String email) {
        // Check if email already registered
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Generate 6-digit OTP
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        // Save OTP token with expiry
        OtpToken token = new OtpToken(email, otp, LocalDateTime.now().plusMinutes(10));
        otpTokenRepository.save(token);

        // Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP for Registration");
        message.setText("Hello,\n\nYour OTP is: " + otp + "\nIt expires in 10 minutes.\n\nRegards,\nDemo App");
        mailSender.send(message);
    }
    public boolean verifyOtp(String email, String otp) {
        return otpTokenRepository.findByEmailAndOtp(email, otp)
            .filter(token -> token.getExpiresAt().isAfter(LocalDateTime.now()))
            .map(token -> {
                otpTokenRepository.deleteByEmail(email); // cleanup
                return true;
            }).orElse(false);
    }
}