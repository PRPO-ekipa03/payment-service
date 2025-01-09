package si.uni.prpo.group03.paymentservice.service.interfaces;

import com.paypal.orders.Order;
import si.uni.prpo.group03.paymentservice.dto.PaymentRequestDTO;
import si.uni.prpo.group03.paymentservice.dto.PaymentResponseDTO;
import si.uni.prpo.group03.paymentservice.dto.PayResponseDTO;

import java.io.IOException;
import java.util.List;

public interface PayPalService {
    String createOrder(PaymentRequestDTO paymentRequest, Long reservationId, Long userId) throws IOException;
    String captureOrder(String orderId);
    Order getOrderDetails(String orderId) throws IOException;
    String cancelOrder(String orderId);
    List<PayResponseDTO> getPaymentsByUserId(Long userId);

}
