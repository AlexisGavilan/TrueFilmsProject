package com.photo.truefilmsproject.controller;

import com.photo.truefilmsproject.dto.CreatePortfolioItemRequest;
import com.photo.truefilmsproject.dto.PortfolioItemResponse;
import com.photo.truefilmsproject.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "http://localhost:3000") // Permite peticiones desde Next.js
public class PortfolioController {

    private final PortfolioService service;

    public PortfolioController(PortfolioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PortfolioItemResponse>> getAllItems() {
        return ResponseEntity.ok(service.getAllItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioItemResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getItemById(id));
    }

    @PostMapping
    public ResponseEntity<PortfolioItemResponse> createItem(@Valid @RequestBody CreatePortfolioItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createItem(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        service.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}