package com.silvestre.web_applicationv1.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvestre.web_applicationv1.Dto.AttachToPaymentDto;
import com.silvestre.web_applicationv1.Dto.PaymentIntentResponse;
import com.silvestre.web_applicationv1.Dto.PaymentMethodDto;
import com.silvestre.web_applicationv1.entity.*;
import com.silvestre.web_applicationv1.enums.PaymentStatus;
import com.silvestre.web_applicationv1.enums.QuotationStatus;
import com.silvestre.web_applicationv1.repository.CalendarAvailabilityRepository;
import com.silvestre.web_applicationv1.service.*;
import jakarta.mail.MessagingException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("api/pay-mongo/transactions")
public class CustomPMPaymentController {


//    https://api.paymongo.com/v1/payment_intents\

    @Autowired
    private QuotationService quotationService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private String paymongoSecretAuthHeader;

    @Autowired
    private MessagingService messagingService;


    @Autowired
    private QuotationRedisService quotationRedisService;

    @Autowired
    private CalendarAvailabilityRepository calendarAvailabilityRepository;

    @Autowired
    private CalendarAvailabilityService calendarAvailabilityService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentV2Service paymentV2Service;

//    http://localhost:8080/api/pay-mongo/transactions/47/102

    @PostMapping("/{userId}/{quotationId}/payment_intents")
    public ResponseEntity<?> sendPaymentIntent(@PathVariable Long userId,@PathVariable Long quotationId,   @RequestBody Map<String, Object> requestBody) {

        Quotation existing = quotationService.findByIdAndUSerId(quotationId, userId);


        // ✅ Use BigDecimal to preserve decimal cents
        BigDecimal pesoAmount = new BigDecimal(requestBody.get("amount").toString());


        BigDecimal amountInCentavo = pesoAmount.multiply(BigDecimal.valueOf(100));

        int amountInInteger = amountInCentavo.intValueExact();

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("amount", amountInInteger);
        System.out.println(amountInInteger);

        String[] payment_method_allowed = {"card", "gcash", "grab_pay", "dob", "qrph"};
        attributes.put("payment_method_allowed", payment_method_allowed
        );

        String statement_descriptor = "Sivestres Exquisite Style and Events";

        attributes.put("statement_descriptor", statement_descriptor);
        attributes.put("capture_type", "automatic");
        attributes.put("currency", "PHP");
        attributes.put("description", "Payment for booking");

        Map<String, Object> cardOptions = new HashMap<>();
        cardOptions.put("request_three_d_secure", "any");
        Map<String, Object> paymentMethodOptions = new HashMap<>();
        paymentMethodOptions.put("card", cardOptions);
        attributes.put("payment_method_options", paymentMethodOptions);


        Map<String, String> metadata = new HashMap<>();
        metadata.put("quotationId", quotationId.toString());
        attributes.put("metadata", metadata);

        data.put("attributes", attributes);


        Map<String, Object> body = new HashMap<>();
        body.put("data", data);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", paymongoSecretAuthHeader);

        System.out.println(body);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        String url = "https://api.paymongo.com/v1/payment_intents";

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        return response;
    }

    @PostMapping("/payment_method")
    public ResponseEntity<?> createPaymentMethodResource(@RequestBody PaymentMethodDto dto){
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> attributes = new HashMap<>();
        Map<String,Object> details = new HashMap<>();
        Map<String,Object> billing= new HashMap<>();

        String type= dto.getType();

        attributes.put("type",type);

        billing.put("name",dto.getName());
        billing.put("email",dto.getEmail());
        billing.put("phone",dto.getPhone());

        if(type.equalsIgnoreCase("card")){
            details.put("card_number",dto.getCard_number());
            details.put("exp_month",dto.getExp_month());
            details.put("exp_year",dto.getExp_year());
            details.put("cvc",dto.getCvc());
            attributes.put("details",details);
        }

        if(type.equalsIgnoreCase("dob")){
            details.put("bank_code",dto.getBank_code());
            attributes.put("details",details);
        }

        data.put("attributes",attributes);


        Map<String, Object> body = new HashMap<>();
        body.put("data", data);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", paymongoSecretAuthHeader);

        System.out.println(body);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        String url = "https://api.paymongo.com/v1/payment_methods";

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );





        return response;
    }


    @PostMapping("/attach-Payment-Intent")
    public ResponseEntity<?> attachPaymentIntent(@RequestBody AttachToPaymentDto dto) throws JsonProcessingException, MessagingException {



        //For email
        String email = dto.getEmail();
        String name= dto.getName();
        String phone =dto.getPhone();

        Map<String, Object> data = new HashMap<>();
        Map<String, Object> attributes = new HashMap<>();

        System.out.println("attach called");
        attributes.put("payment_method",dto.getPayment_method());
        attributes.put("return_url","http://localhost:5173/returnUrl");

        data.put("attributes",attributes);

        //https://api.paymongo.com/v1/payment_intents/{id}/attach

        Map<String, Object> body = new HashMap<>();
        body.put("data", data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", paymongoSecretAuthHeader);


        System.out.println(body);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
//        String url = "https://api.paymongo.com/v1/payment_intents/{id}/attach";
        String url = "https://api.paymongo.com/v1/payment_intents/"+dto.getId()+"/attach";

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Payment API returned " + response.getStatusCode());
        }



        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(response.getBody());

        JsonNode paymentIntent = root.path("data");
        JsonNode attributes1 = paymentIntent.path("attributes");

        JsonNode paymentNode = attributes1.path("payments").get(0).path("attributes");
        String paymentStatus = paymentNode.path("status").asText();
        String sourceType = paymentNode.path("source").path("type").asText();
        System.out.println("Payment Status: " + paymentStatus);
        System.out.println("Source Type: " + sourceType);


        JsonNode paymentsArray = attributes1.path("payments");
        if (paymentsArray == null || !paymentsArray.isArray() || paymentsArray.size() == 0) {
            // No payments yet - return the response as-is (for non-card payments)
            return ResponseEntity.ok(root);
        }


        if (!"card".equals(sourceType) || !"paid".equals(paymentStatus)) {
            System.out.println("Returning raw response - Source: " + sourceType + ", Status: " + paymentStatus);
            return ResponseEntity.ok(root); // Return the PayMongo response as-is
        }

        String quotationIdStr = paymentNode.path("metadata").path("quotationId").asText(null);

        Quotation quotation =quotationService.findById(Long.valueOf(quotationIdStr));


        PaymentV2 paymentV2= paymentV2Service.processSuccessfulPayment(root,quotation.getId(),quotation.getUser().getId());


        quotationRedisService.removeQuotation(Long.valueOf(quotationIdStr));

        quotation.setStatus(QuotationStatus.BOOKED);

        quotationService.save(quotation);

        Payments payment = new Payments();
        payment.setPaymongoPaymentId(paymentIntent.path("id").asText());
        payment.setDescription(paymentNode.path("description").asText());
        payment.setStatus(PaymentStatus.valueOf(paymentNode.path("status").asText().toUpperCase()));


      String transactionId = attributes1.path("payments").get(0).path("id").asText(); // This gets "pay_qT6P14MN21uPSGw4EiKoehMc"
//
//
        Long fee = paymentNode.path("fee").asLong();
        Long netAmount= paymentNode.path("net_amount").asLong();
        Long amount= paymentNode.path("amount").asLong();


        BigDecimal feeInPeso = BigDecimal.valueOf(fee).divide(BigDecimal.valueOf(100));
        BigDecimal netAmountInPeso = BigDecimal.valueOf(netAmount).divide(BigDecimal.valueOf(100));
        BigDecimal amountInPeso = BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(100));
//
        payment.setFee(feeInPeso);
        payment.setNetAmount(netAmountInPeso);
        payment.setAmount(amountInPeso);


        payment.setSourceType(paymentNode.path("source").path("type").asText());
        payment.setStatementDescriptor(paymentNode.path("statement_descriptor").asText());
        payment.setOrigin("api");

        ZonedDateTime philippinesTime = ZonedDateTime.now(ZoneId.of("Asia/Manila"));
        payment.setPaidAt(philippinesTime.toInstant());



        if (quotationIdStr != null) {
//            payment.setQuotation(quotation);
//            payment.setTotalDue(quotation.getTotal());


            if(email.trim().isEmpty()){
                email = quotation.getUser().getEmail();
            }



        }

        LocalDate localDate = quotation.getRequestedEventDate();




        //blocking the date once paid

       CalendarAvailability calendarAvailability= calendarAvailabilityService.createOrUpdate(localDate,"BOOKED",
               "already paid booking fee");


        List<QuotationLineItem> items= quotation.getLineItems();



        messagingService.sendReceipt(email,name,amountInPeso,feeInPeso,netAmountInPeso,"card",transactionId,philippinesTime,items, quotation.getTotal(),
                payment.getDescription());

        messagingService.sendContractLink(email,name,quotationIdStr);


        return response;

    }


}
