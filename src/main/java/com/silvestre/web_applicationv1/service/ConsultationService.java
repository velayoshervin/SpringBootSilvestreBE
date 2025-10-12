package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.Consultation;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.repository.ConsultationRepository;
import com.silvestre.web_applicationv1.repository.UserRepository;
import com.silvestre.web_applicationv1.requests.ConsultationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultationService {

    @Autowired
    private ConsultationRepository repository;

    @Autowired
    private UserRepository userRepository;

    public Consultation create(ConsultationRequest request){
        Long userId = request.getUserId();
        User existing = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("invalid user"));

        Consultation consultation = new Consultation(request);
        consultation.setStatus("SUBMITTED");
        consultation.setUser(existing);

        return  repository.save(consultation);
    }

    public Page<Consultation> findAllSubmittedFirst(Pageable pageable) {
        return repository.findAllSubmittedFirst(pageable);
    }




    public Consultation approve(Long consultationId) {

        Consultation consultation = repository.findById(consultationId).orElseThrow(()->
                new ResourceNotFoundException("consultation doesn't exist"));
        consultation.setStatus("APPROVED");
       return repository.save(consultation);
    }

    public Consultation reject(Long consultationId) {

        Consultation consultation = repository.findById(consultationId).orElseThrow(()->
                new ResourceNotFoundException("consultation doesn't exist"));
        consultation.setStatus("REJECTED");
        return repository.save(consultation);
    }
}
