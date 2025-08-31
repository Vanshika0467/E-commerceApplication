package com.demo.service;

import com.demo.entity.*;
import com.demo.repository.InvoiceRepository;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceService {

    private static final BigDecimal GST_RATE = BigDecimal.valueOf(0.18);
    private static final BigDecimal FLAT_SHIPPING_FEE = BigDecimal.valueOf(50);

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private OrderService orderService;

    /**
     * ✅ Return existing invoice or generate a new one.
     */
    public Invoice getOrCreateInvoice(Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return invoiceRepository.findByOrder(order)
                .orElseGet(() -> generateInvoiceForOrder(order));
    }

    /**
     * 🧾 Generate and persist invoice for given order.
     */
    public Invoice generateInvoiceForOrder(Order order) {
        BigDecimal totalAmount = BigDecimal.valueOf(order.getTotalAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxAmount = calculateTax(totalAmount);
        BigDecimal shippingFee = calculateShipping();

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber(order.getId()));
        invoice.setGeneratedAt(LocalDateTime.now());
        invoice.setTotalAmount(totalAmount);
        invoice.setTaxAmount(taxAmount);
        invoice.setShippingFee(shippingFee);
        invoice.setPaymentMethod(order.getPaymentMethod());
        invoice.setBillingAddress(order.getBillingAddress());
        invoice.setShippingAddress(order.getShippingAddress());
        invoice.setStatus(InvoiceStatus.GENERATED);
        invoice.setOrder(order);
        invoice.setUser(order.getUser());

        return invoiceRepository.save(invoice);
    }

    /**
     * 📄 Generate PDF for invoice.
     */
    public byte[] generateInvoicePdf(Long orderId) {
        Order order = orderService.getOrderById(orderId);
        Invoice invoice = invoiceRepository.findByOrder(order)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found for order ID: " + orderId));

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 14);
                content.setLeading(18f);
                content.newLineAtOffset(50, 750);

                writeInvoiceHeader(content, invoice);
                writeOrderItems(content, order);
                writeInvoiceSummary(content, invoice);

                content.endText();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }

    private void writeInvoiceHeader(PDPageContentStream content, Invoice invoice) throws IOException {
        content.showText("Invoice #: " + invoice.getInvoiceNumber()); content.newLine();
        content.showText("Date: " + invoice.getGeneratedAt().toLocalDate()); content.newLine();
        content.showText("Customer: " + invoice.getUser().getName()); content.newLine();
        content.showText("Billing Address: " + invoice.getBillingAddress()); content.newLine();
        content.showText("Shipping Address: " + invoice.getShippingAddress()); content.newLine();
        content.showText("Payment Method: " + invoice.getPaymentMethod()); content.newLine();
        content.newLine();
        content.showText("Items:"); content.newLine();
    }

    private void writeOrderItems(PDPageContentStream content, Order order) throws IOException {
        for (OrderItem item : order.getOrderItems()) {
            String line = item.getProduct().getName() + " x" + item.getQuantity() + " - Rs." + item.getPrice();
            content.showText(line); content.newLine();
        }
        content.newLine();
    }

    private void writeInvoiceSummary(PDPageContentStream content, Invoice invoice) throws IOException {
        content.showText("Tax: Rs." + invoice.getTaxAmount()); content.newLine();
        content.showText("Shipping: Rs." + invoice.getShippingFee()); content.newLine();
        content.showText("Total: Rs." + invoice.getTotalAmount()); content.newLine();
    }

    private String generateInvoiceNumber(Long orderId) {
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return "INV-" + datePart + "-" + orderId;
    }

    private BigDecimal calculateTax(BigDecimal totalAmount) {
        return totalAmount.multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateShipping() {
        return FLAT_SHIPPING_FEE;
    }
}