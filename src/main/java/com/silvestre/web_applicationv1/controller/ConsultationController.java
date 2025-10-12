package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.Dto.ConsultationDto;
import com.silvestre.web_applicationv1.entity.Consultation;
import com.silvestre.web_applicationv1.requests.ConsultationRequest;
import com.silvestre.web_applicationv1.response.PaginatedResponse;
import com.silvestre.web_applicationv1.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/protected/consultation")
public class ConsultationController {

    @Autowired
    private ConsultationService service;

    @PostMapping()
    public ResponseEntity<?> submitConsultationRequest(@RequestBody ConsultationRequest request){
     Consultation consultation=  service.create(request);

        ConsultationDto response = new ConsultationDto(consultation);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllConsultations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Consultation> consultationsPage = service.findAllSubmittedFirst(pageable);

        Page<ConsultationDto> dtoPage = consultationsPage.map(ConsultationDto::new);
        PaginatedResponse<ConsultationDto> response = new PaginatedResponse<>(dtoPage);

        return ResponseEntity.ok(response);
    }



    @PutMapping("/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> accept(@RequestParam Long consultationId){
        Consultation consultation = service.approve(consultationId);
        ConsultationDto dto = new ConsultationDto(consultation);

        return  ResponseEntity.ok(dto);
    }

    @PutMapping("/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reject(@RequestParam Long consultationId){
        Consultation consultation = service.reject(consultationId);
        ConsultationDto dto = new ConsultationDto(consultation);

        return  ResponseEntity.ok(dto);
    }


}
