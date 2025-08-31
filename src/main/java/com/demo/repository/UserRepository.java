package com.demo.repository;

import com.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ find user by email (useful for login/checking duplicates)
    Optional<User> findByEmail(String email);

    // ✅ check if user exists by email
    boolean existsByEmail(String email);
}