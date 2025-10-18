package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.Dto.BookingReportDTO;
import com.silvestre.web_applicationv1.Dto.RevenueByMonthDTO;
import com.silvestre.web_applicationv1.Dto.StatusCountDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ReportRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<BookingReportDTO> findBookingsReport(Map<String, Object> filters) {
        String sql = "SELECT b.id, " +
                "CONCAT(u.firstname, ' ', u.lastname) AS customer, " +
                "b.booking_status, b.total_amount, b.balance, " +
                "b.created_at, v.name AS venue, pck.package_name AS package " +
                "FROM booking b " +
                "JOIN users u ON b.user_id = u.user_id " +
                "JOIN quotation q ON b.quotation_id = q.quotation_id " +
                "JOIN venue v ON q.venue_id = v.venue_id " +
                "JOIN packages pck ON q.package_id = pck.package_id " +
                buildWhereClause(filters) +
                "ORDER BY b.created_at DESC LIMIT 500";

        Query query = entityManager.createNativeQuery(sql, "BookingReportMapping");
        setParameters(query, filters);
        return query.getResultList();
    }

    public List<BookingReportDTO> findPaymentsReport(Map<String, Object> filters) {
        String sql = "SELECT p.id, " +
                "b.id AS booking_id, " +
                "CONCAT(u.firstname, ' ', u.lastname) AS customer, " +
                "p.amount, p.status, p.paid_at, " +
                "v.name AS venue " +
                "FROM payments p " +
                "JOIN booking b ON p.booking_id = b.id " +
                "JOIN users u ON b.user_id = u.user_id " +
                "JOIN quotation q ON b.quotation_id = q.quotation_id " +
                "JOIN venue v ON q.venue_id = v.venue_id " +
                buildWhereClause(filters) +
                "ORDER BY p.paid_at DESC LIMIT 500";

        Query query = entityManager.createNativeQuery(sql, "BookingReportMapping");
        setParameters(query, filters);
        return query.getResultList();
    }

    public List<BookingReportDTO> findUsersReport(Map<String, Object> filters) {
        String sql = "SELECT u.user_id, " +
                "u.firstname, u.lastname, u.email, " +
                "COUNT(b.id) AS total_bookings, " +
                "COALESCE(SUM(b.total_amount), 0) AS total_spent " +
                "FROM users u " +
                "LEFT JOIN booking b ON u.user_id = b.user_id " +
                "LEFT JOIN quotation q ON b.quotation_id = q.quotation_id " +
                "LEFT JOIN venue v ON q.venue_id = v.venue_id " +
                buildWhereClause(filters) +
                "GROUP BY u.user_id " +
                "ORDER BY total_bookings DESC LIMIT 500";

        Query query = entityManager.createNativeQuery(sql, "BookingReportMapping");
        setParameters(query, filters);
        return query.getResultList();
    }

    public List<StatusCountDTO> findBookingsByStatus(Map<String, Object> filters) {
        String sql = "SELECT b.booking_status, COUNT(*) AS total " +
                "FROM booking b " +
                "JOIN quotation q ON b.quotation_id = q.quotation_id " +
                "JOIN venue v ON q.venue_id = v.venue_id " +
                buildWhereClause(filters) +
                "GROUP BY b.booking_status";

        Query query = entityManager.createNativeQuery(sql, "StatusCountMapping");
        setParameters(query, filters);
        return query.getResultList();
    }

    public List<RevenueByMonthDTO> findRevenueByMonth(Map<String, Object> filters) {
        String sql = "SELECT DATE_FORMAT(p.paid_at, '%Y-%m') AS month, " +
                "SUM(p.amount) AS revenue " +
                "FROM payments p " +
                "JOIN booking b ON p.booking_id = b.id " +
                "JOIN quotation q ON b.quotation_id = q.quotation_id " +
                "JOIN venue v ON q.venue_id = v.venue_id " +
                "WHERE p.status = 'paid' " + buildWhereClause(filters) +
                "GROUP BY month ORDER BY month";

        Query query = entityManager.createNativeQuery(sql, "RevenueByMonthMapping");
        setParameters(query, filters);
        return query.getResultList();
    }

    private String buildWhereClause(Map<String, Object> filters) {
        List<String> conditions = new ArrayList<>();

        if (filters.containsKey("start")) {
            conditions.add("DATE(b.created_at) >= :start");
        }
        if (filters.containsKey("end")) {
            conditions.add("DATE(b.created_at) <= :end");
        }
        if (filters.containsKey("status")) {
            conditions.add("b.booking_status = :status");
        }
        if (filters.containsKey("venue")) {
            conditions.add("v.name = :venue");
        }

        return conditions.isEmpty() ? "" : "WHERE " + String.join(" AND ", conditions) + " ";
    }

    private void setParameters(Query query, Map<String, Object> filters) {
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
    }
}