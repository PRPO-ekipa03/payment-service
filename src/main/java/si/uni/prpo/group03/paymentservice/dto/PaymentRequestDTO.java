package si.uni.prpo.group03.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO representing a payment request")
public class PaymentRequestDTO {

    @Schema(description = "Payment amount", example = "100.00")
    private Double amount;

    @Schema(description = "Currency code (e.g., USD)", example = "USD")
    private String currency;

    @Schema(description = "Description of the payment", example = "Payment for order #12345")
    private String description;

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
}
