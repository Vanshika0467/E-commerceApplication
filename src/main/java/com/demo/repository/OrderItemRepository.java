package com.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	boolean existsByProduct_Id(Long productId);

	List<OrderItem> findByOrderId(Long orderId);


}
