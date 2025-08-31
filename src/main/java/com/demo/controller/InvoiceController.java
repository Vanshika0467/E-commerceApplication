package com.demo.controller;

import com.demo.entity.Invoice;
import com.demo.service.InvoiceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    /**
     * ✅ Return invoice details for a given order.
     * If invoice already exists, return it. Otherwise, generate and persist.
     */
    @GetMapping("/generateInvoice/{orderId}")
    public ResponseEntity<Invoice> getInvoiceByOrderId(@PathVariable Long orderId) {
        Invoice invoice = invoiceService.getOrCreateInvoice(orderId);
        return ResponseEntity.ok(invoice);
    }

    /**
     * 📄 Return invoice PDF as a downloadable file.
     */
    @GetMapping("/{orderId}/download")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long orderId) {
        byte[] pdfBytes = invoiceService.generateInvoicePdf(orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
            .filename("invoice_" + orderId + ".pdf").build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    /**
     * 🖥 Optional: Preview invoice PDF inline (no download).
     */
    @GetMapping("/{orderId}/preview")
    public ResponseEntity<byte[]> previewInvoicePdf(@PathVariable Long orderId) {
        byte[] pdfBytes = invoiceService.generateInvoicePdf(orderId);

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);
    }
}