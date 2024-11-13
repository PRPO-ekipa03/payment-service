package si.uni.prpo.group03.paymentservice.client;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.Order;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.OrdersGetRequest;
import com.paypal.orders.OrderRequest;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class PayPalClient {

    private final PayPalHttpClient payPalHttpClient;

    public PayPalClient(PayPalHttpClient payPalHttpClient) {
        this.payPalHttpClient = payPalHttpClient;
    }

    public Order createOrder(OrderRequest orderRequest) throws IOException {
        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
        HttpResponse<Order> response = payPalHttpClient.execute(request);

        if (response.statusCode() == 201) {
            return response.result();
        } else {
            throw new IOException("Failed to create PayPal order. Status Code: " + response.statusCode());
        }
    }

    public Order captureOrder(String orderId) throws IOException {
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
        HttpResponse<Order> response = payPalHttpClient.execute(request);

        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return response.result();
        } else {
            throw new IOException("Failed to capture PayPal order. Status Code: " + response.statusCode());
        }
    }

    public Order getOrderDetails(String orderId) throws IOException {
        OrdersGetRequest request = new OrdersGetRequest(orderId);
        HttpResponse<Order> response = payPalHttpClient.execute(request);

        if (response.statusCode() == 200) {
            return response.result();
        } else {
            throw new IOException("Failed to retrieve PayPal order details. Status Code: " + response.statusCode());
        }
    }
}
