package si.uni.prpo.group03.paymentservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.sql.Timestamp;

@Entity
@Table(name = "payments")
@Schema(description = "Entity representing a payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the payment", example = "1")
    private Long id;

    @Column(name = "paypal_order_id", nullable = false, unique = true)
    @NotNull(message = "PayPal order ID is required")
    @Schema(description = "Unique PayPal order ID associated with the payment", example = "PAYPAL123456789")
    private String paypalOrderId;

    @Column(nullable = false)
    @NotNull(message = "Amount is required")
    @Schema(description = "Amount paid", example = "99.99")
    private Double amount;

    @Column(nullable = false, length = 3)
    @NotNull(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency code should be exactly 3 characters (e.g., USD)")
    @Schema(description = "Currency code of the payment", example = "USD")
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Status is required")
    @Schema(
        description = "Status of the payment",
        example = "CREATED",
        allowableValues = {"CREATED", "CANCELED", "CAPTURED"}
    )
    private PaymentStatus status;

    @Column(length = 255)
    @Size(max = 255, message = "Description should not exceed 255 characters")
    @Schema(description = "Optional description of the payment", example = "Payment for order #12345")
    private String description;

    @Column(name = "user_id", nullable = false)
    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the user who made the payment", example = "42")
    private Long userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @NotNull
    @Schema(description = "Timestamp when the payment was created", example = "2023-08-15T10:15:30Z")
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    @NotNull
    @Schema(description = "Timestamp when the payment was last updated", example = "2023-08-15T12:00:00Z")
    private Timestamp updatedAt;

    @Column(name = "reservation_id", nullable = false)
    @NotNull
    @Schema(description = "ID of the associated reservation", example = "789")
    private Long reservationId;

    // Enum representing payment status
    public enum PaymentStatus {
        CREATED,
        CANCELED,
        CAPTURED
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPaypalOrderId() { return paypalOrderId; }
    public void setPaypalOrderId(String paypalOrderId) { this.paypalOrderId = paypalOrderId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
}
