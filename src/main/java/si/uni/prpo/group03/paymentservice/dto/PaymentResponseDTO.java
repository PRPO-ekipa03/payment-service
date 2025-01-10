package si.uni.prpo.group03.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO representing the payment initiation response")
public class PaymentResponseDTO {

    @Schema(description = "Approval URL for the payment", example = "https://paypal.com/approve?token=...")
    private String approvalUrl;

    @Schema(description = "Order ID assigned to the payment", example = "ORDER123456789")
    private String orderId;

    @Schema(description = "Status of the payment transaction", example = "CREATED")
    private String status;

    // Getters and Setters
    public String getApprovalUrl() {
        return approvalUrl;
    }

    public void setApprovalUrl(String approvalUrl) {
        this.approvalUrl = approvalUrl;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
