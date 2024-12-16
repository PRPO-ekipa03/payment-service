package si.uni.prpo.group03.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlePaymentNotFoundException(PaymentNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(OrderRetrievalException.class)
    public ResponseEntity<String> handleOrderRetrievalException(OrderRetrievalException ex) {
        if (ex.getMessage().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } else if (ex.getMessage().contains("service unavailable")) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("The order service is temporarily unavailable. Please try again later.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving the order: " + ex.getMessage());
        }
    }

    @ExceptionHandler(InvalidPaymentStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidPaymentStatusException(InvalidPaymentStatusException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(OrderCreationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleOrderCreationException(OrderCreationException ex) {
        return "Order creation failed: " + ex.getMessage();
    }

    @ExceptionHandler(OrderCaptureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleOrderCaptureException(OrderCaptureException ex) {
        return "Order capture failed: " + ex.getMessage();
    }
}

