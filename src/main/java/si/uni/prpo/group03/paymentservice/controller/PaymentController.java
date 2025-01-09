package si.uni.prpo.group03.paymentservice.controller;

import si.uni.prpo.group03.paymentservice.dto.PayResponseDTO;
import si.uni.prpo.group03.paymentservice.dto.PaymentRequestDTO;
import si.uni.prpo.group03.paymentservice.dto.PaymentResponseDTO;
import si.uni.prpo.group03.paymentservice.service.interfaces.PayPalService;
import com.paypal.orders.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PayPalService payPalService;

    @Autowired
    public PaymentController(PayPalService payPalService) {
        this.payPalService = payPalService;
    }

    // Method expects to get reservatinID as query parameter
    @PostMapping("/create")
    public ResponseEntity<String> createOrder(
        @RequestBody PaymentRequestDTO paymentRequest, 
        @RequestParam(name = "reservationId", required = true) Long reservationId,
        @RequestHeader("X-User-Id") String xUserId
    ) {
        try {
            Long userId = Long.parseLong(xUserId); // Convert userId from header to Long
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

    @GetMapping("/success")
    public ResponseEntity<String> handleSuccess(@RequestParam("token") String orderId) { // `token` is the same as `orderId`
        String result = payPalService.captureOrder(orderId);  // Capture by orderId directly
        if (result.contains("successfully")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(500).body(result);
        }
    }

    // For testing purposes with a hardcoded userId
    @GetMapping("/cancel")
    public ResponseEntity<String> handleCancel(@RequestParam("token") String orderId) {
        String result = payPalService.cancelOrder(orderId);  
        return ResponseEntity.ok(result);
    }
    

    // Endpoint to retrieve order details by order ID
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

    @GetMapping("/user")
    public ResponseEntity<List<PayResponseDTO>> getPaymentsByUserId(
            @RequestHeader("X-User-Id") String xUserId) {
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
