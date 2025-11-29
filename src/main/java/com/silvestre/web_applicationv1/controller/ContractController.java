package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.Dto.ContractResponse;
import com.silvestre.web_applicationv1.Dto.SignatureRequest;
import com.silvestre.web_applicationv1.entity.Payments;
import com.silvestre.web_applicationv1.entity.Quotation;
import com.silvestre.web_applicationv1.service.ContractService;
import com.silvestre.web_applicationv1.service.QuotationService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/contracts")
@CrossOrigin(origins = "http://localhost:5173")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @Autowired
    private QuotationService quotationService;


    @GetMapping("/{quotationId}")
    public ResponseEntity<ContractResponse> getContract(@PathVariable Long quotationId) {
        try {
            Quotation quotation = quotationService.findById(quotationId);

            ContractResponse response = new ContractResponse();
            response.setQuotationId(quotation.getId());
            response.setCustomerName(quotation.getCustomerName());
            response.setContactNumber(quotation.getContactNumber());
            response.setEventDate(quotation.getRequestedEventDate());
            response.setEventType(quotation.getEventType());
            response.setPax(quotation.getPax());
            response.setVenue(getVenueName(quotation));
            response.setCelebrants(quotation.getCelebrants());
            response.setTotalAmount(quotation.getTotal());
            response.setLineItems(quotation.getLineItems());
            response.setContractSigned(quotation.getContractSigned());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    // Submit signature
    @PostMapping("/{quotationId}/sign")
    public ResponseEntity<String> signContract(
            @PathVariable Long quotationId,
            @RequestBody SignatureRequest request) {

        try {
            contractService.processSignature(quotationId, request.getSignatureData(), request.getSignerName());
            return ResponseEntity.ok("Contract signed successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error signing contract: " + e.getMessage());
        }
    }

    private String getVenueName(Quotation quotation) {
        if (quotation.getVenue() != null) {
            return quotation.getVenue().getName();
        }
        return quotation.getClientVenue() != null ? quotation.getClientVenue() : "Client's Venue";
    }



}
