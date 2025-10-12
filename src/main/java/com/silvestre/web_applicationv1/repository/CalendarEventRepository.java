package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.CalendarEvent;
import com.silvestre.web_applicationv1.entity.Quotation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent,Long> {

    @EntityGraph(attributePaths = {"attendees", "eventCreator", "booking"})
    Optional<CalendarEvent> findWithDetailsByEventId(Long eventId);


    @Query("SELECT e FROM CalendarEvent e JOIN e.invitations i WHERE i.user.id = :userId")
    List<CalendarEvent> findAllEventsUserIsInvitedTo(@Param("userId") Long userId);

    @Query("SELECT e FROM CalendarEvent e JOIN e.invitations i " +
            "WHERE i.user.id = :userId AND i.status = 'ACCEPTED'")
    List<CalendarEvent> findAcceptedEventsForUser(@Param("userId") Long userId);

    @Query("SELECT DISTINCT e FROM CalendarEvent e " +
            "LEFT JOIN e.invitations i " +
            "WHERE e.eventCreator.id = :userId OR i.user.id = :userId")
    List<CalendarEvent> findAllVisibleEventsForUser(@Param("userId") Long userId);

    Optional<CalendarEvent> findByQuotation(Quotation quotation);

}
