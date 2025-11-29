package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.ExceptionHandler.MailSenderException;
import com.silvestre.web_applicationv1.entity.Payments;
import com.silvestre.web_applicationv1.entity.Quotation;
import com.silvestre.web_applicationv1.entity.QuotationLineItem;
import com.silvestre.web_applicationv1.util.EmailMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class MessagingService {


//    frontend.verify-token-url=${FRONTEND_VERIFY_TOKEN_URL}
//    frontend.sign-contract-url=${FRONTEND_SIGN_CONTRACT_URL}

    @Value("${frontend.verify-token-url}")
    private String verifyTokenUrl;


    @Value("${frontend.sign-contract-url}")
    private String signContractUrl;

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;


    public void sendReceipt(String email, String customerName, BigDecimal amount, BigDecimal fee,
                            BigDecimal netAmount, String paymentMethod, String transactionId,
                            ZonedDateTime paymentDate, List<QuotationLineItem> lineItems,BigDecimal Total,
                            String description) {

        try {
            Context context = new Context();

            // Set variables with safe defaults
            context.setVariable("customerName", customerName != null ? customerName : "Valued Customer");
            context.setVariable("amount", amount != null ? amount : BigDecimal.ZERO);
            context.setVariable("fee", fee != null ? fee : BigDecimal.ZERO);
            context.setVariable("netAmount", netAmount != null ? netAmount : BigDecimal.ZERO);
            context.setVariable("paymentMethod", paymentMethod != null ? paymentMethod : "Online Payment");
            context.setVariable("transactionId", transactionId != null ? transactionId : "N/A");
            context.setVariable("paymentDate", paymentDate != null ? paymentDate : ZonedDateTime.now());
            context.setVariable("lineItems", lineItems != null ? lineItems : new ArrayList<>());
            context.setVariable("Total", Total != null ? Total : BigDecimal.ZERO );


            // DEBUG: Check what's actually in your line items
            System.out.println("=== LINE ITEMS DEBUG ===");
            System.out.println("Line items count: " + (lineItems != null ? lineItems.size() : "null"));
            if (lineItems != null && !lineItems.isEmpty()) {
                for (int i = 0; i < lineItems.size(); i++) {
                    QuotationLineItem item = lineItems.get(i);
                    System.out.println("Item " + i + ":");
                    System.out.println("  - Description: " + item.getDescription());
                    System.out.println("  - Quantity: " + item.getQuantity());
                    System.out.println("  - Price: " + item.getPriceAtQuotation());
                    System.out.println("  - Item object: " + (item.getItem() != null ? "present" : "null"));
                    if (item.getItem() != null) {
                        System.out.println("  - Item name: " + item.getItem().getName());
                    }
                }
            }
            System.out.println("=== END DEBUG ===");

            // Process template
            String htmlContent = templateEngine.process("Receipt", context);

            // Create and send email
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Payment Receipt - Transaction #" + (transactionId != null ? transactionId : ""));
            helper.setText(htmlContent, true);
            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new MailSenderException("Failed to send receipt email: " + e.getMessage());
        } catch (Exception e) {
            throw new MailSenderException("Failed to process receipt template: " + e.getMessage());
        }
    }

    public void sendOtp(String otp, String name){
        Context context= new Context();
        context.setVariable("otp", otp);
        context.setVariable("name", name);
        try{
            String htmlContent=templateEngine.process("Otp",context);
            MimeMessage message= javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            helper.setTo("velayoshervin69@gmail.com");
            helper.setSubject("One Time Password");
            helper.setText(htmlContent,true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new MailSenderException("failed to send OTP message. something went wrong");
        }
    }

    public void sendToken(String token, String name, String email){
        //add the ff : email

        //"http://localhost:5173/verify?token="
        String link = verifyTokenUrl +"="+ token;

        Context context= new Context();
        context.setVariable("name", name);
        context.setVariable("link",link);

        try{
        String htmlContent=templateEngine.process("EnableAccount",context);
            MimeMessage message= javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            helper.setTo(email);
            helper.setSubject("Activate Account");
            helper.setText(htmlContent,true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new MailSenderException("failed to send message. something went wrong");
        }
    }

    public void sendContractConfirmation(Quotation quotation) {
        try {
            Context context = new Context();

            // Contract details
            context.setVariable("customerName", quotation.getCustomerName());
            context.setVariable("eventDate", quotation.getRequestedEventDate());
            context.setVariable("eventType", quotation.getEventType());
            context.setVariable("venue", quotation.getVenue().getName());
            context.setVariable("pax", quotation.getPax());
            context.setVariable("totalAmount", quotation.getTotal());
            context.setVariable("lineItems", quotation.getLineItems());
            context.setVariable("signerName", quotation.getSignerName());
            context.setVariable("signedAt", quotation.getSignedAt());
            context.setVariable("signatureData", quotation.getSignatureData());



            // Payment terms calculations
            BigDecimal totalAmount = quotation.getTotal();
//            BigDecimal amountPaid = payment.getAmount();
            BigDecimal amountPaid = BigDecimal.ZERO;
            BigDecimal balanceDue = totalAmount.subtract(amountPaid);
            BigDecimal percentagePaid = amountPaid.divide(totalAmount, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            LocalDate balanceDueDate = quotation.getRequestedEventDate().minusDays(30);

            context.setVariable("amountPaid", amountPaid);
            context.setVariable("balanceDue", balanceDue);
            context.setVariable("percentagePaid", percentagePaid);
            context.setVariable("balanceDueDate", balanceDueDate);

            // Process email template
            String htmlContent = templateEngine.process("ContractConfirmation", context);

            // Send email
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(quotation.getUser().getEmail());
            helper.setSubject("Contract Signed - " + quotation.getCustomerName());
            helper.setText(htmlContent, true);


            // Embed the signature image
            if (quotation.getSignatureData() != null && !quotation.getSignatureData().isEmpty()) {
                // Convert Base64 to byte array
                String base64Image = quotation.getSignatureData().split(",")[1];
                byte[] signatureBytes = Base64.getDecoder().decode(base64Image);

                // Add as inline image with CID
                helper.addInline("signatureImage", new ByteArrayResource(signatureBytes), "image/png");

                // Replace the HTML to use CID reference
                htmlContent = htmlContent.replace("th:src=\"${signatureData}\"", "src=\"cid:signatureImage\"");
            }

            helper.setText(htmlContent, true);


            javaMailSender.send(message);

        } catch (Exception e) {
            throw new MailSenderException("Failed to send contract confirmation: " + e.getMessage());
        }
    }


    public void sendContractLink(String email, String name, String quotationIdStr) throws MessagingException {
        Context context = new Context();

        //"http://localhost:5173/sign-contract?quotationId="

        String link = signContractUrl+ "="+ quotationIdStr;

        context.setVariable("customer", name);
        context.setVariable("link", link);



        String htmlContent = templateEngine.process("SendContractLink", context);

        // Send email
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject("PaymentLink");
        helper.setText(htmlContent, true);


        javaMailSender.send(message);

    }
}
