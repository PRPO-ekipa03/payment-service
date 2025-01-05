package si.uni.prpo.group03.paymentservice.exception;

public class OrderCaptureException extends RuntimeException {

    public OrderCaptureException(String message) {
        super(message);
    }

    public OrderCaptureException(String message, Throwable cause) {
        super(message, cause);
    }
}
