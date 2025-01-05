package si.uni.prpo.group03.paymentservice.config;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalConfig {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Value("${paypal.success.url}")
    private String successUrl;

    @Value("${paypal.cancel.url}")
    private String cancelUrl;

    @Bean
    public PayPalHttpClient payPalHttpClient() {
        PayPalEnvironment environment;

        if ("sandbox".equalsIgnoreCase(mode)) {
            environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
        } else {
            environment = new PayPalEnvironment.Live(clientId, clientSecret);
        }

        return new PayPalHttpClient(environment);
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }
}


