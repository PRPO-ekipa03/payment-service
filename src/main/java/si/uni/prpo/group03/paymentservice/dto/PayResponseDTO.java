package si.uni.prpo.group03.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import si.uni.prpo.group03.paymentservice.model.Payment.PaymentStatus;
import java.sql.Timestamp;

@Schema(description = "Response DTO representing payment details for a PayPal transaction.")
public class PayResponseDTO {

    @Schema(description = "PayPal order ID", example = "PAYPAL123456")
    private String paypalOrderId; // PayPal order ID

    @Schema(description = "Payment amount", example = "99.99")
    private Double amount;        // Payment amount

    @Schema(description = "Currency code (e.g., USD)", example = "USD")
    private String currency;      // Currency code (e.g., USD)

    @Schema(description = "Payment description", example = "Payment for reservation #123")
    private String description;   // Payment description

    @Schema(
        description = "Payment status",
        example = "CREATED",
        allowableValues = {"CREATED", "CANCELED", "CAPTURED"}
    )
    private PaymentStatus status;

    @Schema(description = "User ID associated with the payment", example = "42")
    private Long userId;          // User ID associated with the payment

    @Schema(description = "Reservation ID associated with the payment", example = "123")
    private Long reservationId;   // Reservation ID associated with the payment

    @Schema(description = "Timestamp when the payment was created", example = "2023-08-15T14:30:00Z")
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

    public PaymentStatus getStatus() {
        return status;
    }
 
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

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
