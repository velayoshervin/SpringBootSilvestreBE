package com.silvestre.web_applicationv1.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PaginatedResponse<T>{
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

        public PaginatedResponse(Page<T> pageData) {
            this.content = pageData.getContent();
            this.page = pageData.getNumber();
            this.size = pageData.getSize();
            this.totalElements = pageData.getTotalElements();
            this.totalPages = pageData.getTotalPages();
            this.last = pageData.isLast();
        }   
}
