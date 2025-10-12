package com.silvestre.web_applicationv1.controller;


import com.silvestre.web_applicationv1.Dto.NFPostResponseDTO;
import com.silvestre.web_applicationv1.entity.NFPost;
import com.silvestre.web_applicationv1.repository.UserRepository;
import com.silvestre.web_applicationv1.requests.NFPostRequest;
import com.silvestre.web_applicationv1.response.PaginatedResponse;
import com.silvestre.web_applicationv1.service.NFPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/posts")
public class NFPostController {

    @Autowired
    private NFPostService service;



    @GetMapping
    public ResponseEntity<?> getRecentPosts( @PageableDefault(size =10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){

        //recent posts
        Page<NFPost> posts = service.getAllPosts(pageable);

        Page<NFPostResponseDTO> postDto = posts.map(NFPostResponseDTO::new);

        return ResponseEntity.ok(new PaginatedResponse<NFPostResponseDTO>(postDto));
    }

//    @PostMapping
//    public ResponseEntity<?> createPost(@RequestBody NFPostRequest nfPostRequest){
//        return ResponseEntity.ok(service.createPost(nfPostRequest));
//    }

}
