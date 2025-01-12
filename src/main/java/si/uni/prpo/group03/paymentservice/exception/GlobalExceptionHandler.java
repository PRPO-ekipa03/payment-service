package si.uni.prpo.group03.paymentservice.exception;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public String handlePaymentNotFoundException(PaymentNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(OrderRetrievalException.class)
    @ApiResponses({
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "503", description = "Order service unavailable"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> handleOrderRetrievalException(OrderRetrievalException ex) {
        if (ex.getMessage().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } else if (ex.getMessage().contains("service unavailable")) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                 .body("The order service is temporarily unavailable. Please try again later.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("An error occurred while retrieving the order: " + ex.getMessage());
        }
    }

    @ExceptionHandler(InvalidPaymentStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(responseCode = "400", description = "Invalid payment status")
    public String handleInvalidPaymentStatusException(InvalidPaymentStatusException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(OrderCreationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(responseCode = "500", description = "Order creation failed due to server error")
    public String handleOrderCreationException(OrderCreationException ex) {
        return "Order creation failed: " + ex.getMessage();
    }

    @ExceptionHandler(OrderCaptureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(responseCode = "500", description = "Order capture failed due to server error")
    public String handleOrderCaptureException(OrderCaptureException ex) {
        return "Order capture failed: " + ex.getMessage();
    }
}
