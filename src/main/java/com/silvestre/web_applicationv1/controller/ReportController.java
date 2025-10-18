package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/data")
    public ResponseEntity<?> getReportData(
            @RequestParam String action,
            @RequestParam(defaultValue = "bookings") String reportType,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String venue) {

        try {
            Map<String, Object> filters = new HashMap<>();
            if (start != null) filters.put("start", start);
            if (end != null) filters.put("end", end);
            if (status != null) filters.put("status", status);
            if (venue != null) filters.put("venue", venue);

            Object result = reportService.getReportData(action, reportType, filters);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}