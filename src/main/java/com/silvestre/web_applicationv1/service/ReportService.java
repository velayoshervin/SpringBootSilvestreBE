package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.Dto.BookingReportDTO;
import com.silvestre.web_applicationv1.Dto.RevenueByMonthDTO;
import com.silvestre.web_applicationv1.Dto.StatusCountDTO;
import com.silvestre.web_applicationv1.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public Object getReportData(String action, String reportType, Map<String, Object> filters) {
        switch (action) {
            case "bookings_table":
                return getBookingsTableData(reportType, filters);
            case "bookings_by_status":
                return getBookingsByStatus(filters);
            case "revenue_by_month":
                return getRevenueByMonth(filters);
            default:
                return Collections.emptyList();
        }
    }

    public List<BookingReportDTO> getBookingsTableData(String reportType, Map<String, Object> filters) {
        switch (reportType) {
            case "bookings":
                return reportRepository.findBookingsReport(filters);
            case "payments":
                return reportRepository.findPaymentsReport(filters);
            case "users":
                return reportRepository.findUsersReport(filters);
            default:
                return reportRepository.findBookingsReport(filters);
        }
    }

    public List<StatusCountDTO> getBookingsByStatus(Map<String, Object> filters) {
        return reportRepository.findBookingsByStatus(filters);
    }

    public List<RevenueByMonthDTO> getRevenueByMonth(Map<String, Object> filters) {
        return reportRepository.findRevenueByMonth(filters);
    }
}