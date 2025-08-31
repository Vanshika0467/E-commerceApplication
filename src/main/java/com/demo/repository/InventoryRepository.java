package com.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.entity.Inventory;
import com.demo.entity.Product;
@Repository
public interface InventoryRepository  extends JpaRepository<Inventory, Long>{

	Optional<Inventory> findByProductId(Long productId); // ✅ correct

	Inventory findByProduct(Product product);
	

}
