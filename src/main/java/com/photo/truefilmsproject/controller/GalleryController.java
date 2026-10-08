package com.photo.truefilmsproject.controller;

import com.photo.truefilmsproject.entity.Event;
import com.photo.truefilmsproject.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gallery")
@CrossOrigin(origins = "http://localhost:3000") // Permite llamadas desde Next.js
public class GalleryController {

    private final EventService eventService;

    public GalleryController(EventService eventService) {
        this.eventService = eventService;
    }

    // GET /api/gallery/BODA-2026
    @GetMapping("/{code}")
    public ResponseEntity<Event> getGalleryByCode(@PathVariable String code) {
        Event event = eventService.getEventByAccessCode(code);
        return ResponseEntity.ok(event);
    }
}