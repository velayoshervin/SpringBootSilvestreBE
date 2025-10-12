package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.LineItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineItemRepository extends JpaRepository<LineItem,Long> {
}
