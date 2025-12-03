package com.example.expensetracker.controller;

import com.example.expensetracker.dto.TagSuggestionDto;
import com.example.expensetracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TransactionService transactionService;

    @Autowired
    public TagController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Search tags by partial name match
     * GET /tags/search?q=food&limit=10
     */
    @GetMapping("/search")
    public ResponseEntity<List<TagSuggestionDto>> searchTags(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer limit) {
        List<TagSuggestionDto> tags = transactionService.searchTags(q, limit);
        return ResponseEntity.ok(tags);
    }

    /**
     * Get top tags by usage count
     * GET /tags/top?limit=25
     */
    @GetMapping("/top")
    public ResponseEntity<List<TagSuggestionDto>> getTopTags(
            @RequestParam(defaultValue = "25") int limit) {
        return ResponseEntity.ok(transactionService.getTopTags(limit));
    }
}

