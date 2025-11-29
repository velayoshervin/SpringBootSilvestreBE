package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.entity.Payments;
import com.silvestre.web_applicationv1.entity.Quotation;
import com.silvestre.web_applicationv1.repository.QuotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ContractService {

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private MessagingService messagingService;


    public void processSignature(Long quotationId, String signatureData, String signerName) {
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new RuntimeException("Quotation not found with id: " + quotationId));

        // Save signature to database
        quotation.setSignatureData(signatureData);
        quotation.setSignerName(signerName);
        quotation.setSignedAt(LocalDateTime.now());
        quotation.setContractSigned(true);

        quotationRepository.save(quotation);

        // Send confirmation email
        messagingService.sendContractConfirmation(quotation);
    }

}
