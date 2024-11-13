package si.uni.prpo.group03.paymentservice.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import si.uni.prpo.group03.paymentservice.client.PayPalClient;
import si.uni.prpo.group03.paymentservice.dto.PaymentRequestDTO;
import si.uni.prpo.group03.paymentservice.model.Payment;
import si.uni.prpo.group03.paymentservice.model.Payment.PaymentStatus;
import si.uni.prpo.group03.paymentservice.mapper.PaymentMapper;
import si.uni.prpo.group03.paymentservice.repository.PaymentRepository;
import si.uni.prpo.group03.paymentservice.service.interfaces.PayPalService;
import si.uni.prpo.group03.paymentservice.config.PayPalConfig;
import com.paypal.orders.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayPalServiceImpl implements PayPalService {

    private static final Logger logger = LoggerFactory.getLogger(PayPalServiceImpl.class);

    private final PayPalClient payPalClient;
    private final RabbitTemplate rabbitTemplate;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final String successUrl;
    private final String cancelUrl;

    public PayPalServiceImpl(PayPalClient payPalClient, PaymentRepository paymentRepository, 
                             PaymentMapper paymentMapper, PayPalConfig payPalConfig, RabbitTemplate rabbitTemplate) {
        this.payPalClient = payPalClient;
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.successUrl = payPalConfig.getSuccessUrl();
        this.cancelUrl = payPalConfig.getCancelUrl();
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public String createOrder(PaymentRequestDTO paymentRequest, Long reservationId) throws IOException {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        ApplicationContext applicationContext = new ApplicationContext()
                .cancelUrl(cancelUrl)
                .returnUrl(successUrl)
                .brandName("Event4U")
                .landingPage("BILLING")
                .userAction("PAY_NOW");
        orderRequest.applicationContext(applicationContext);

        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        purchaseUnits.add(new PurchaseUnitRequest()
                .amountWithBreakdown(new AmountWithBreakdown()
                        .currencyCode(paymentRequest.getCurrency())
                        .value(String.format("%.2f", paymentRequest.getAmount())))
                .description(paymentRequest.getDescription())
        );
        orderRequest.purchaseUnits(purchaseUnits);

        Order createdOrder = payPalClient.createOrder(orderRequest);
        String paypalOrderId = createdOrder.id();

        Payment payment = paymentMapper.toPayment(paymentRequest);
        payment.setUserId(1L); // Set user ID accordingly - 1 used for testing
        payment.setPaypalOrderId(paypalOrderId);
        payment.setStatus(PaymentStatus.CREATED);
        payment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        payment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        payment.setReservationId(reservationId);
        paymentRepository.save(payment);

        for (LinkDescription link : createdOrder.links()) {
            if ("approve".equals(link.rel())) {
                return link.href(); // Redirect URL for approval
            }
        }
        return null;
    }

    @Override
    public String captureOrder(String orderId) {
        Payment payment = paymentRepository.findByPaypalOrderId(orderId);

        if (payment == null || payment.getStatus() != PaymentStatus.CREATED) {
            return "No matching order found or order already processed.";
        }

        try {
            Order capturedOrder = payPalClient.captureOrder(orderId);
            if (capturedOrder != null) {
                updatePaymentStatus(capturedOrder.id(), "CAPTURED");
                payment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                notifyPaymentConfirmed(payment.getReservationId()); //Notify venue service that the user has paid
                return "Order captured successfully. Order ID: " + capturedOrder.id();
            }
            return "Failed to capture order.";
        } catch (IOException e) {
            logger.error("Error capturing order: {}", e.getMessage());
            return "Error capturing order: " + e.getMessage();
        }
    }

    @Override
    public Order getOrderDetails(String orderId) throws IOException {
        return payPalClient.getOrderDetails(orderId);
    }

    @Override
    public String cancelOrder(String orderId) {
        // Look up the Payment record by PayPal order ID
        Payment payment = paymentRepository.findByPaypalOrderId(orderId);

        // Check if the payment exists and is still in 'CREATED' status
        if (payment == null || payment.getStatus() != PaymentStatus.CREATED) {
            return "No matching order found or order already processed.";
        }

        // Update the payment status to 'CANCELED' and save it
        payment.setStatus(PaymentStatus.CANCELED);
        payment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        paymentRepository.save(payment);

        return "The payment has been canceled successfully.";
    }


    private void updatePaymentStatus(String paypalOrderId, String status) {
        Payment payment = paymentRepository.findByPaypalOrderId(paypalOrderId);
        if (payment != null) {
            payment.setStatus(PaymentStatus.valueOf(status));
            payment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            paymentRepository.save(payment);
        }
    }

    public void notifyPaymentConfirmed(Long reservationId) {
        rabbitTemplate.convertAndSend("paymentExchange", "payment.confirmed", reservationId);
    }
    
}
