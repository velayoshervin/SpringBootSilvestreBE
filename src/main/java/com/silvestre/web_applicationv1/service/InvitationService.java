package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.Invitation;
import com.silvestre.web_applicationv1.repository.InvitationRepository;
import org.springframework.stereotype.Service;

@Service
public class InvitationService {

    private InvitationRepository repository;

    public Invitation create(Invitation invitation){
       return repository.save(invitation);
    }

    public Invitation findById(Long id){
        return repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("invitation not found"));
    }

    public Invitation update(Invitation invitation){
        return  repository.save(invitation);
    }
}
