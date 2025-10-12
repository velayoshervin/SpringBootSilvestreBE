package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.NFPost;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.repository.NFPostRepository;
import com.silvestre.web_applicationv1.repository.UserRepository;
import com.silvestre.web_applicationv1.requests.NFPostRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NFPostService {

    @Autowired
    private NFPostRepository nfPostRepository;

    @Autowired
    private UserRepository userRepository;



    public Page<NFPost> getAllPosts(Pageable pageable){
        return nfPostRepository.findAll(pageable);
    }


}
