package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
}
