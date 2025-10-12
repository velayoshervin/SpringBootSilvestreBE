package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.Event;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event,Long> {
}
