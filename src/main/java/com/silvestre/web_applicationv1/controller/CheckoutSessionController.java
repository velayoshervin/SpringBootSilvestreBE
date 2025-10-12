package com.silvestre.web_applicationv1.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvestre.web_applicationv1.Payments.CheckoutSessionRequest;
import com.silvestre.web_applicationv1.Paymongo.PayMongoService;
import com.silvestre.web_applicationv1.entity.Quotation;
import com.silvestre.web_applicationv1.enums.PaymentStatus;
import com.silvestre.web_applicationv1.repository.PaymentRepository;
import com.silvestre.web_applicationv1.repository.QuotationRepository; // or service to fetch quotation
import com.silvestre.web_applicationv1.requests.PartialPaymentRequest;
import com.silvestre.web_applicationv1.service.ReferenceNumberGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/checkout")
public class CheckoutSessionController {


    @Autowired
    private ReferenceNumberGeneratorService referenceNumberGeneratorService;

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/{quotationId}")
    public ResponseEntity<?> createCheckout(@PathVariable Long quotationId, @RequestParam String eventDate
    ,@RequestParam Long amount ) throws Exception {



        // 1️⃣ Fetch quotation from DB
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));

        BigDecimal clientAmount = BigDecimal.valueOf(amount);

        BigDecimal totalAmount = quotation.getTotalAmount();

       BigDecimal alreadyPaidInCentavos= paymentRepository.sumOfAllPaymentsForQuotation(quotationId, PaymentStatus.PAID);
       BigDecimal alreadyPaid= alreadyPaidInCentavos.divide(BigDecimal.valueOf(100));

        List<CheckoutSessionRequest.LineItem> lineItems = new ArrayList<>();

        CheckoutSessionRequest.Attributes attributes = new CheckoutSessionRequest.Attributes();
        BigDecimal remainingBalanceToPay= totalAmount.subtract(alreadyPaid);

        if(clientAmount.compareTo(remainingBalanceToPay) != 0)
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("amount mismatch expected:"+remainingBalanceToPay +"\nreceived:"+clientAmount);

        if(alreadyPaid.compareTo(BigDecimal.ZERO) == 0){
        attributes.setAmount(quotation.getTotalAmount().longValue());
           lineItems= quotation.getLineItems().stream()
                   .map(li -> new CheckoutSessionRequest.LineItem(
                           li.getDescription(),
                           li.getItem().getDescription(),
                           li.getPriceAtQuotation().multiply(BigDecimal.valueOf(100)).intValueExact(),
                           li.getQuantity(),
                           "PHP"
                   ))
                   .toList();

            String desc= getDescription(quotation,"full",remainingBalanceToPay,BigDecimal.ZERO);
            attributes.setDescription(desc);
            attributes.setShow_line_items(true);

       }else{
            String desc = getDescription(quotation,"remaining",remainingBalanceToPay,alreadyPaid);
            String lineItemDesc = "payment for remaining balance of quotation " +quotation.getId();
            lineItems = createLineItems(quotation,lineItemDesc,remainingBalanceToPay);
            attributes.setShow_line_items(false);
            attributes.setDescription(desc);
        }

//        attributes.setAmount(remainingBalanceToPay.multiply(BigDecimal.valueOf(100)).longValueExact());
        String quotationPrefix= "QUO-" + quotation.getId();
        String quotationRef= referenceNumberGeneratorService.generateRef(quotationPrefix);



        attributes.setReference_number(quotationRef);
        attributes.setSend_email_receipt(true);

        attributes.setShow_description(true);
        attributes.setSuccess_url("http://localhost:5173/user-dashboard/payments");
        attributes.setLine_items(lineItems);
        attributes.setPayment_method_types(List.of(
                "card", "gcash", "qrph", "billease", "dob", "dob_ubp",
                "brankas_bdo", "brankas_landbank", "brankas_metrobank",
                "grab_pay", "paymaya"
        ));
        attributes.setMetadata(Map.of(
                "quotationId", String.valueOf(quotation.getId()),
                "userId", String.valueOf(quotation.getUser().getId()),
                "paymentType" ,"fully paid"
                ,"eventDate", eventDate
        ));
        // 4️⃣ Wrap in Data
        CheckoutSessionRequest.Data data = new CheckoutSessionRequest.Data();
        data.setAttributes(attributes);

        // 5️⃣ Wrap in CheckoutSessionRequest
        CheckoutSessionRequest request = new CheckoutSessionRequest();
        request.setData(data);

        // 6️⃣ Send to PayMongo
        ResponseEntity<String> response = payMongoService.createCheckoutSession(request);
        System.out.println("PayMongo response: " + response);

        // 7️⃣ Extract checkout URL
        String checkoutUrl = new ObjectMapper()
                .readTree(response.getBody())
                .path("data").path("attributes").path("checkout_url")
                .asText();

        // 8️⃣ Return checkout URL to frontend
        return ResponseEntity.ok(Map.of("checkout_url", checkoutUrl));
    }

    @Autowired
    private PayMongoService payMongoService;

    @Autowired
    private QuotationRepository quotationRepository;

    @PostMapping("/partial-payment")
    public ResponseEntity<?> handleReservationPayments(@RequestBody PartialPaymentRequest partialPaymentRequest, @RequestParam String paymentType) throws JsonProcessingException {

        Quotation quotation = quotationRepository.findById(partialPaymentRequest.getQuotationId())
                .orElseThrow(() -> new RuntimeException("Quotation not found"));

        BigDecimal Total = quotation.getTotal();

        double percent= Objects.equals(paymentType, "reservation") ? 0.1 : paymentType.equals("book")? 0.2 : 0;

        if(percent == 0)
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("invalid partial payment option");

        BigDecimal computedPercentage = Total.multiply(BigDecimal.valueOf(percent));

        BigDecimal clientAmount = BigDecimal.valueOf(partialPaymentRequest.getAmount());

        if (clientAmount.compareTo(computedPercentage) != 0) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("mismatch amount");
        }

        computedPercentage= computedPercentage.multiply(BigDecimal.valueOf(100));

        Long amount = computedPercentage.longValueExact();

        List<CheckoutSessionRequest.LineItem> lineItems = new ArrayList<>();

        String lineItemDesc="";
        if(paymentType.equals("reservation")){
            lineItemDesc= "reservation fee";
        } else if (paymentType.equals("book")) {
            lineItemDesc= "booking fee";
        }

        lineItems= createLineItems(quotation,lineItemDesc,clientAmount);


        BigDecimal alreadyPaidInCentavos= paymentRepository.sumOfAllPaymentsForQuotation(quotation.getId(), PaymentStatus.PAID);
        BigDecimal alreadyPaid= alreadyPaidInCentavos.divide(BigDecimal.valueOf(100));

        String quotationPrefix= "QUO-" + quotation.getId();
        String quotationRef= referenceNumberGeneratorService.generateRef(quotationPrefix);

        // 3️⃣ Build Attributes
        CheckoutSessionRequest.Attributes attributes = new CheckoutSessionRequest.Attributes();

        String desc= getDescription(quotation,paymentType,clientAmount,alreadyPaid);
        attributes.setDescription(desc);

//        attributes.setDescription("Payment for Quotation #" + quotation.getId() +": "+paymentType);
        attributes.setAmount(amount);
        attributes.setReference_number(quotationRef);
        attributes.setSend_email_receipt(true);
        attributes.setShow_line_items(true);
        attributes.setShow_description(true);
        attributes.setSuccess_url("http://localhost:5173/user-dashboard/payments");
        attributes.setLine_items(lineItems);
        attributes.setPayment_method_types(List.of(
                "card", "gcash", "qrph", "billease", "dob", "dob_ubp",
                "brankas_bdo", "brankas_landbank", "brankas_metrobank",
                "grab_pay", "paymaya"
        ));
        attributes.setMetadata(Map.of(
                "quotationId", String.valueOf(quotation.getId()),
                "userId", String.valueOf(quotation.getUser().getId()),
                "paymentType", paymentType
                ,"eventDate",partialPaymentRequest.getEventDate()
        ));
        // 4️⃣ Wrap in Data
        CheckoutSessionRequest.Data data = new CheckoutSessionRequest.Data();
        data.setAttributes(attributes);

        // 5️⃣ Wrap in CheckoutSessionRequest
        CheckoutSessionRequest request = new CheckoutSessionRequest();
        request.setData(data);

        // 6️⃣ Send to PayMongo
        ResponseEntity<String> response = payMongoService.createCheckoutSession(request);
        System.out.println("PayMongo response: " + response);

        // 7️⃣ Extract checkout URL
        String checkoutUrl = new ObjectMapper()
                .readTree(response.getBody())
                .path("data").path("attributes").path("checkout_url")
                .asText();

        // 8️⃣ Return checkout URL to frontend
        return ResponseEntity.ok(Map.of("checkout_url", checkoutUrl));

    }

    private String getDescription(Quotation quotation, String paymentType, BigDecimal amountToCharge,BigDecimal alreadyPaid) {
        BigDecimal totalAmount = quotation.getTotalAmount();

        switch (paymentType.toLowerCase()) {
            case "reservation":
                return String.format("RESERVATION FEE (10%%) - Quotation #%d\n" +
                                "• This ₱%,.2f payment secures your booking date\n" +
                                "• Total event cost: ₱%,.2f\n" +
                                "• Remaining balance: ₱%,.2f",
                        quotation.getId(), amountToCharge, totalAmount,
                        totalAmount.subtract(amountToCharge));

            case "book":
                return String.format("BOOKING PAYMENT (20%%) - Quotation #%d\n" +
                                "• This ₱%,.2f payment confirms your booking\n" +
                                "• Total event cost: ₱%,.2f",
                        quotation.getId(), amountToCharge, totalAmount);

            case "remaining":
                return String.format("FINAL PAYMENT - Quotation #%d\n" +
                                "• Paying remaining balance: ₱%,.2f\n" +
                                "• Already paid: ₱%,.2f\n" +
                                "• Total event cost: ₱%,.2f",
                        quotation.getId(), amountToCharge, alreadyPaid, totalAmount);

            case "full":
                return String.format("FULL PAYMENT - Quotation #%d\n" +
                                "• One-time payment of ₱%,.2f",
                        quotation.getId(), amountToCharge);
            default:
                return "Payment for Quotation #" + quotation.getId();
        }
    }

    private List<CheckoutSessionRequest.LineItem> createLineItems(Quotation quotation,String paymentDesc, BigDecimal amountToCharge)
    {
        CheckoutSessionRequest.LineItem lineItem = new CheckoutSessionRequest.LineItem();
        lineItem.setAmount(amountToCharge.multiply(BigDecimal.valueOf(100)).intValueExact());
        lineItem.setQuantity(1);
        lineItem.setName(paymentDesc);
        lineItem.setCurrency("PHP");

        return List.of(lineItem);
    }


}
