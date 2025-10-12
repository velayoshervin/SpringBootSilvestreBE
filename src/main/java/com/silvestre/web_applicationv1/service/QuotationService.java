package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.Quotation;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.repository.QuotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class  QuotationService {

    @Autowired
    private QuotationRepository quotationRepository;
    @Autowired
    private UserService userService;

    public Quotation save(Quotation quotation){
        return quotationRepository.save(quotation);
    }

    public List<Quotation> loadAllQuotations(Long userId){
        return quotationRepository.findAllByUserId(userId);
    }

    public Quotation findByIdAndUSerId(Long quotationId, Long userId){
        return quotationRepository.findByIdAndUserId(quotationId, userId).
                orElseThrow(()-> new ResourceNotFoundException("quotation not found"));
    }

    public Page<Quotation> findAll(Pageable pageable) {
        return quotationRepository.findAll(pageable);
    }

    public Page<Quotation> findByUserId(Long userId, Pageable pageable) {

        User existing = userService.findUserById(userId);
        return quotationRepository.findByUserId(userId, pageable);


    }

    public Quotation findById(Long quotationId){
        return quotationRepository.findById(quotationId).orElseThrow(()-> new ResourceNotFoundException("quotation not found"));
    }

}
