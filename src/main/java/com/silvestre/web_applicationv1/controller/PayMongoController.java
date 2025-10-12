package com.silvestre.web_applicationv1.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silvestre.web_applicationv1.Dto.PaymentCustomerDto;
import com.silvestre.web_applicationv1.entity.Payments;
import com.silvestre.web_applicationv1.repository.PaymentRepository;
import com.silvestre.web_applicationv1.response.PaginatedResponse;
import com.silvestre.web_applicationv1.response.PaymentResponseDto;
import com.silvestre.web_applicationv1.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paymongo")
public class PayMongoController {

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Paymongo-Signature") String signature) {
        try {
            System.out.println("Webhook payload: " + payload);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);


            String eventType = root.path("data").path("attributes").path("type").asText();

            // ✅ Only handle payment-related events
            if (eventType.startsWith("payment.paid")) {
                paymentService.processWebhook(payload,signature);
            }

            return ResponseEntity.ok("Webhook received");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing webhook");
        }
    }

    @GetMapping("/payments")
    public ResponseEntity<?> getAllPayments(@RequestParam (defaultValue = "0") int page,
                                            @RequestParam  (defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page,size);

        Page<Payments> response = paymentRepository.findAll(pageable);

        Page<PaymentResponseDto> responseDto = response.map(PaymentResponseDto::new);
       return ResponseEntity.ok(new PaginatedResponse<>(responseDto));


    }

    // ✅ Get payments by user ID (paginated)
    @GetMapping("/payments/user/{userId}")
    public ResponseEntity<?> getPaymentsByUserId(@PathVariable Long userId,
                                                              @RequestParam (defaultValue = "0") int page,
                                                              @RequestParam  (defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page,size,Sort.by(Sort.Order.desc("createdAt")));
        Page<Payments> payments = paymentRepository.findByUserId(userId, pageable);

        Page<PaymentCustomerDto> response = payments.map(PaymentCustomerDto::new);

        return ResponseEntity.ok(new PaginatedResponse<>(response));
    }

    // ✅ Optional: get payment by ID
    @GetMapping("/payments/{id}")
    public ResponseEntity<Payments> getPaymentById(@PathVariable Long id) {
        return paymentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}



