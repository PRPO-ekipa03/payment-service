package si.uni.prpo.group03.paymentservice.service.interfaces;

import com.paypal.orders.Order;
import si.uni.prpo.group03.paymentservice.dto.PaymentRequestDTO;
import java.io.IOException;

public interface PayPalService {
    String createOrder(PaymentRequestDTO paymentRequest, Long reservationId) throws IOException;
    String captureOrder(String orderId);
    Order getOrderDetails(String orderId) throws IOException;
    String cancelOrder(String orderId);
}
