package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.MenuBundle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuBundleRepository extends JpaRepository<MenuBundle,Long> {
    boolean existsByName(String name);

    List<MenuBundle> findByActiveTrue();
}
