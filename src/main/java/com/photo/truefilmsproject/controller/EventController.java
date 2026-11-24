package com.photo.truefilmsproject.controller;

import com.photo.truefilmsproject.dto.EventRequest;
import com.photo.truefilmsproject.dto.EventResponse;
import com.photo.truefilmsproject.service.EventService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:3000")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        EventResponse created = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping(value = "/{id}/banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventResponse> uploadBannerImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(eventService.uploadBannerImage(id, file));
    }

    @GetMapping("/{id}/banner")
    public ResponseEntity<Resource> getBannerImage(@PathVariable Long id) {
        Resource resource = eventService.getBannerResource(id);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = "application/octet-stream";
        try {
            String probed = Files.probeContentType(resource.getFile().toPath());
            if (probed != null) {
                contentType = probed;
            }
        } catch (Exception ignored) {
            // Si no se puede detectar el MIME type, usa el valor por defecto
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}