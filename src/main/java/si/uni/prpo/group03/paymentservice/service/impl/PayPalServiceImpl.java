package si.uni.prpo.group03.paymentservice.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import si.uni.prpo.group03.paymentservice.client.PayPalClient;
import si.uni.prpo.group03.paymentservice.dto.PayResponseDTO;
import si.uni.prpo.group03.paymentservice.dto.PaymentRequestDTO;
import si.uni.prpo.group03.paymentservice.dto.PaymentResponseDTO;
import si.uni.prpo.group03.paymentservice.model.Payment;
import si.uni.prpo.group03.paymentservice.model.Payment.PaymentStatus;
import si.uni.prpo.group03.paymentservice.mapper.PaymentMapper;
import si.uni.prpo.group03.paymentservice.repository.PaymentRepository;
import si.uni.prpo.group03.paymentservice.service.interfaces.PayPalService;
import si.uni.prpo.group03.paymentservice.config.PayPalConfig;
import si.uni.prpo.group03.paymentservice.exception.*;

import com.paypal.orders.*;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayPalServiceImpl implements PayPalService {

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
    public String createOrder(PaymentRequestDTO paymentRequest, Long reservationId, Long userId) {
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

        try {
            Order createdOrder = payPalClient.createOrder(orderRequest);
            String paypalOrderId = createdOrder.id();

            Payment payment = paymentMapper.toPayment(paymentRequest);
            payment.setUserId(userId); // Use provided userId
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
        } catch (IOException e) {
            throw new OrderCreationException("Failed to create PayPal order", e);
        }
    }

    @Override
    public String captureOrder(String orderId) {
        Payment payment = paymentRepository.findByPaypalOrderId(orderId);

        if (payment == null) {
            throw new PaymentNotFoundException("No payment found for orderId: " + orderId);
        }
        if (payment.getStatus() != PaymentStatus.CREATED) {
            throw new InvalidPaymentStatusException("Payment already processed or canceled for orderId: " + orderId);
        }

        try {
            Order capturedOrder = payPalClient.captureOrder(orderId);
            if (capturedOrder != null) {
                updatePaymentStatus(capturedOrder.id(), "CAPTURED");
                payment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                notifyPaymentConfirmed(payment.getReservationId()); // Notify venue service that the user has paid
                return "Order captured successfully. Order ID: " + capturedOrder.id();
            }
            throw new OrderCaptureException("Failed to capture order with orderId: " + orderId);
        } catch (IOException e) {
            throw new OrderCaptureException("Error capturing PayPal order", e);
        }
    }

    @Override
    public Order getOrderDetails(String orderId) {
        try {
            return payPalClient.getOrderDetails(orderId);
        } catch (IOException e) {
            throw new OrderRetrievalException("Failed to retrieve PayPal order details", e);
        }
    }

    @Override
    public String cancelOrder(String orderId) {
        Payment payment = paymentRepository.findByPaypalOrderId(orderId);

        if (payment == null) {
            throw new PaymentNotFoundException("No payment found for orderId: " + orderId);
        }
        if (payment.getStatus() != PaymentStatus.CREATED) {
            throw new InvalidPaymentStatusException("Cannot cancel a payment that is not in CREATED status.");
        }

        payment.setStatus(PaymentStatus.CANCELED);
        payment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        paymentRepository.save(payment);

        return "The payment has been canceled successfully.";
    }

    @Override
    public List<PayResponseDTO> getPaymentsByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream()
                .map(paymentMapper::toPayResponseDTO) // Use the mapper here
                .collect(Collectors.toList());
    }

    private void updatePaymentStatus(String paypalOrderId, String status) {
        Payment payment = paymentRepository.findByPaypalOrderId(paypalOrderId);
        if (payment == null) {
            throw new PaymentNotFoundException("No payment found for orderId: " + paypalOrderId);
        }
        payment.setStatus(PaymentStatus.valueOf(status));
        payment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        paymentRepository.save(payment);
    }

    public void notifyPaymentConfirmed(Long reservationId) {
        rabbitTemplate.convertAndSend("paymentExchange", "payment.confirmed", reservationId);
    }
}
