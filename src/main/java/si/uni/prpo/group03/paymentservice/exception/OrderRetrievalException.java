package si.uni.prpo.group03.paymentservice.exception;

public class OrderRetrievalException extends RuntimeException {
    public OrderRetrievalException(String message) {
        super(message);
    }

    public OrderRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}