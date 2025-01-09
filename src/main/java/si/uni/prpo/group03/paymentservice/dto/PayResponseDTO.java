package si.uni.prpo.group03.paymentservice.dto;

import java.sql.Timestamp;

import si.uni.prpo.group03.paymentservice.model.Payment.PaymentStatus;

public class PayResponseDTO {

    private String paypalOrderId; // PayPal order ID
    private Double amount;        // Payment amount
    private String currency;      // Currency code (e.g., USD)
    private String description;   // Payment description
    private PaymentStatus status;
    private Long userId;          // User ID associated with the payment
    private Long reservationId;   // Reservation ID associated with the payment
    private Timestamp createdAt;  // Timestamp when the payment was created

    // Getters and Setters
    public String getPaypalOrderId() {
        return paypalOrderId;
    }

    public void setPaypalOrderId(String paypalOrderId) {
        this.paypalOrderId = paypalOrderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
