package com.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.entity.Invoice;
import com.demo.entity.InvoiceStatus;
import com.demo.entity.Order;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByUserId(Long userId);
    List<Invoice> findByStatus(InvoiceStatus status);
    
    // ✅ Added for PDF generation
    Optional<Invoice> findByOrder(Order order);
}