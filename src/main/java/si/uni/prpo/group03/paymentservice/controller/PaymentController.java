package si.uni.prpo.group03.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import si.uni.prpo.group03.paymentservice.dto.PayResponseDTO;
import si.uni.prpo.group03.paymentservice.dto.PaymentRequestDTO;
import si.uni.prpo.group03.paymentservice.dto.PaymentResponseDTO;
import si.uni.prpo.group03.paymentservice.service.interfaces.PayPalService;
import com.paypal.orders.Order;

import java.io.IOException;
import java.util.List;

@Tag(name = "Payments", description = "Controller for managing PayPal-based payments")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PayPalService payPalService;

    @Autowired
    public PaymentController(PayPalService payPalService) {
        this.payPalService = payPalService;
    }

    @Operation(summary = "Create a new PayPal order", 
               description = "Creates a PayPal order based on the provided payment request and reservation ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order created successfully (returns approval URL)"),
        @ApiResponse(responseCode = "500", description = "Failed to create order")
    })
    @PostMapping("/create")
    public ResponseEntity<String> createOrder(
            @RequestBody PaymentRequestDTO paymentRequest,
            @RequestParam(name = "reservationId", required = true) Long reservationId,
            @RequestHeader("X-User-Id") String xUserId
    ) {
        try {
            Long userId = Long.parseLong(xUserId);
            String approvalUrl = payPalService.createOrder(paymentRequest, reservationId, userId);
            if (approvalUrl != null) {
                return ResponseEntity.ok(approvalUrl); // Return URL to redirect user for approval
            } else {
                return ResponseEntity.status(500).body("Failed to create order");
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error creating order: " + e.getMessage());
        }
    }

    @Operation(summary = "Capture a PayPal order", 
               description = "Captures the PayPal order after successful user approval.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order captured successfully"),
        @ApiResponse(responseCode = "500", description = "Error capturing order")
    })
    @GetMapping("/success")
    public ResponseEntity<String> handleSuccess(@RequestParam("token") String orderId) {
        String result = payPalService.captureOrder(orderId);  // Capture by orderId directly
        if (result.contains("successfully")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(500).body(result);
        }
    }

    @Operation(summary = "Cancel a PayPal order", 
               description = "Cancels an existing PayPal order if it is still in CREATED status.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order canceled successfully"),
        @ApiResponse(responseCode = "500", description = "Error canceling order")
    })
    @GetMapping("/cancel")
    public ResponseEntity<String> handleCancel(@RequestParam("token") String orderId) {
        String result = payPalService.cancelOrder(orderId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Retrieve PayPal order details", 
               description = "Fetches order details by order ID directly from PayPal.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order details retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Failed to retrieve order details")
    })
    @GetMapping("/details/{orderId}")
    public ResponseEntity<Order> getOrderDetails(@PathVariable String orderId) {
        try {
            Order order = payPalService.getOrderDetails(orderId);
            if (order != null) {
                return ResponseEntity.ok(order);
            } else {
                return ResponseEntity.status(404).body(null); // Order not found
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(summary = "Retrieve payments for a user", 
               description = "Fetches all payments associated with the specified user ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No payments found for the user"),
        @ApiResponse(responseCode = "500", description = "Error retrieving payments")
    })
    @GetMapping("/user")
    public ResponseEntity<List<PayResponseDTO>> getPaymentsByUserId(
            @RequestHeader("X-User-Id") String xUserId
    ) {
        try {
            Long userId = Long.parseLong(xUserId);
            List<PayResponseDTO> payments = payPalService.getPaymentsByUserId(userId);
            if (payments != null && !payments.isEmpty()) {
                return ResponseEntity.ok(payments);
            } else {
                return ResponseEntity.status(404).body(null); // No payments found
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // Handle unexpected errors
        }
    }
}
