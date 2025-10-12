package com.silvestre.web_applicationv1.Paymongo;

import com.silvestre.web_applicationv1.Payments.CheckoutSessionRequest;
import com.silvestre.web_applicationv1.entity.Quotation;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@EnableConfigurationProperties
@Service
public class PayMongoService {

    @Autowired
    private PayMongoProperties payMongoProperties;
    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void checkKeys() {
        if (payMongoProperties.getSecretKey() == null || payMongoProperties.getSecretKey().isEmpty()) {
            throw new RuntimeException("PayMongo secret key is not set! Check application.properties or environment variables.");
        }
        System.out.println("PayMongo secret key loaded: " + payMongoProperties.getSecretKey().substring(0, 5) + "...");
    }

    public ResponseEntity<String> createCheckoutSession(CheckoutSessionRequest checkoutSessionRequest) {
        String url = payMongoProperties.getBaseUrl() + payMongoProperties.getCheckoutSessionEndpoint();
        String secretKey = payMongoProperties.getSecretKey();
        HttpHeaders httpHeaders =new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(payMongoProperties.getSecretKey(),payMongoProperties.getSecretKey());



        HttpEntity<CheckoutSessionRequest> httpEntity = new HttpEntity<>(checkoutSessionRequest,httpHeaders);

        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }

    public CheckoutSessionRequest buildCheckoutRequestFromQuotation(Quotation quotation) {
        // 1. Convert quotation line items
        List<CheckoutSessionRequest.LineItem> lineItems = quotation.getLineItems().stream()
                .map(li -> {
                    int amountInCents = li.getPriceAtQuotation()
                            .multiply(BigDecimal.valueOf(100))
                            .intValueExact();
                    return new CheckoutSessionRequest.LineItem(
                            li.getDescription(),
                            li.getItem().getDescription(),
                            amountInCents,
                            li.getQuantity(),
                            "PHP"
                    );
                })
                .toList();

        // 2. Create Attributes
        CheckoutSessionRequest.Attributes attributes = new CheckoutSessionRequest.Attributes();
        attributes.setDescription("Payment for Quotation #" + quotation.getId());
        attributes.setReference_number("QUO-" + quotation.getId());
        attributes.setSuccess_url("http://localhost:5173/payment-success");
        attributes.setCancel_url("http://localhost:5173/payment-cancel");
        attributes.setSend_email_receipt(false);
        attributes.setShow_line_items(true);
        attributes.setShow_description(true);
        attributes.setPayment_method_types(List.of());
        attributes.setLine_items(lineItems);
        attributes.setMetadata(Map.of("quotationId", quotation.getId()));
        attributes.setPayment_method_types(List.of(
                "card", "gcash", "qrph", "billease", "dob", "dob_ubp",
                "brankas_bdo", "brankas_landbank", "brankas_metrobank",
                "grab_pay", "paymaya"
        ));

        // 3. Wrap in Data
        CheckoutSessionRequest.Data data = new CheckoutSessionRequest.Data();
        data.setAttributes(attributes);

        // 4. Wrap in CheckoutSessionRequest
        CheckoutSessionRequest request = new CheckoutSessionRequest();
        request.setData(data);

        return request;
    }
}
