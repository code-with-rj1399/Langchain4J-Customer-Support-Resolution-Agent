package com.rj1399.customersupport.api;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/demo-cases")
public class DemoCaseController {

    private final JdbcTemplate jdbcTemplate;

    public DemoCaseController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<DemoCaseResponse> demoCases() {
        return jdbcTemplate.query("""
                SELECT o.order_number,
                       o.total_amount,
                       o.status AS order_status,
                       c.name AS customer_name,
                       COALESCE(p.status, 'UNKNOWN') AS payment_status,
                       t.subject AS ticket_subject
                FROM orders o
                JOIN customers c ON c.id = o.customer_id
                LEFT JOIN payments p ON p.order_id = o.id
                LEFT JOIN support_tickets t ON t.order_id = o.id
                WHERE o.order_number IN ('1001','1002','1003','1004','1005','1006','1007','1008','1009','1010','1011')
                ORDER BY o.order_number
                """, (rs, rowNum) -> new DemoCaseResponse(
                rs.getString("order_number"),
                rs.getBigDecimal("total_amount"),
                rs.getString("order_status"),
                rs.getString("customer_name"),
                rs.getString("payment_status"),
                rs.getString("ticket_subject"),
                promptFor(rs.getString("order_number"), rs.getString("order_status"), rs.getString("ticket_subject"))
        ));
    }

    private String promptFor(String orderNumber, String status, String ticketSubject) {
        if (ticketSubject != null && !ticketSubject.isBlank()) {
            return "Please investigate order " + orderNumber + ". " + ticketSubject + ".";
        }
        return switch (status) {
            case "DELAYED" -> "Please investigate order " + orderNumber + ". The customer is asking about the delivery delay and refund eligibility.";
            case "SHIPPED" -> "Where is order " + orderNumber + "? Please check the current delivery status.";
            case "DELIVERED" -> "Please investigate order " + orderNumber + " and help with the customer's delivery question.";
            case "CANCELLED" -> "Please check the cancellation and refund status for order " + orderNumber + ".";
            default -> "Please investigate order " + orderNumber + ".";
        };
    }

    public record DemoCaseResponse(
            String orderNumber,
            BigDecimal amount,
            String status,
            String customerName,
            String paymentStatus,
            String ticketSubject,
            String prompt
    ) {}
}
