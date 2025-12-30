package com.example.expensetracker.controller;

import com.example.expensetracker.dto.SalaryCycleDto;
import com.example.expensetracker.dto.SalaryCycleTotalsDto;
import com.example.expensetracker.service.SalaryCycleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary-cycles")
public class SalaryCycleController {

    private static final Logger logger = LoggerFactory.getLogger(SalaryCycleController.class);

    private final SalaryCycleService salaryCycleService;

    public SalaryCycleController(SalaryCycleService salaryCycleService) {
        this.salaryCycleService = salaryCycleService;
    }

    /**
     * Get all salary cycles
     *
     * GET /api/salary-cycles
     *
     * Response:
     * [
     *   {
     *     "cycleId": 1,
     *     "label": "Jan Salary Cycle (5 Jan – 4 Feb)",
     *     "startDate": "2025-01-05",
     *     "endDate": "2025-02-04",
     *     "salaryAmount": 50000.0
     *   }
     * ]
     */
    @GetMapping
    public ResponseEntity<List<SalaryCycleDto>> getAllSalaryCycles() {
        logger.info("GET /api/salary-cycles");

        List<SalaryCycleDto> cycles = salaryCycleService.getAllSalaryCycles();

        logger.info("Returning {} salary cycles", cycles.size());
        return ResponseEntity.ok(cycles);
    }

    /**
     * Get salary cycle by ID
     *
     * GET /api/salary-cycles/{cycleId}
     */
    @GetMapping("/{cycleId}")
    public ResponseEntity<SalaryCycleDto> getSalaryCycleById(@PathVariable Long cycleId) {
        logger.info("GET /api/salary-cycles/{}", cycleId);

        return salaryCycleService.getSalaryCycleById(cycleId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get totals for a specific salary cycle
     *
     * GET /api/salary-cycles/{cycleId}/totals
     *
     * Response:
     * {
     *   "totalCredit": 52000,
     *   "totalDebit": 43000,
     *   "netSavings": 9000,
     *   "salaryAmount": 50000
     * }
     */
    @GetMapping("/{cycleId}/totals")
    public ResponseEntity<SalaryCycleTotalsDto> getSalaryCycleTotals(@PathVariable Long cycleId) {
        logger.info("GET /api/salary-cycles/{}/totals", cycleId);

        try {
            SalaryCycleTotalsDto totals = salaryCycleService.calculateSalaryCycleTotals(cycleId);
            return ResponseEntity.ok(totals);
        } catch (IllegalArgumentException e) {
            logger.error("Salary cycle not found: {}", cycleId);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Detect and create salary cycles from existing transactions
     *
     * POST /api/salary-cycles/detect
     */
    @PostMapping("/detect")
    public ResponseEntity<String> detectSalaryCycles() {
        logger.info("POST /api/salary-cycles/detect");

        salaryCycleService.detectAndCreateSalaryCycles();

        return ResponseEntity.ok("Salary cycles detected and created successfully");
    }

    /**
     * Refresh salary cycles (delete and re-create)
     *
     * POST /api/salary-cycles/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshSalaryCycles() {
        logger.info("POST /api/salary-cycles/refresh");

        salaryCycleService.refreshSalaryCycles();

        return ResponseEntity.ok("Salary cycles refreshed successfully");
    }

    /**
     * Update last salary cycle end date to current date
     *
     * POST /api/salary-cycles/update-last
     */
    @PostMapping("/update-last")
    public ResponseEntity<String> updateLastSalaryCycle() {
        logger.info("POST /api/salary-cycles/update-last");

        salaryCycleService.updateLastSalaryCycleEndDate();

        return ResponseEntity.ok("Last salary cycle updated successfully");
    }
}

