package com.photo.truefilmsproject.controller;

import com.photo.truefilmsproject.dto.EventRequest;
import com.photo.truefilmsproject.dto.EventResponse;
import com.photo.truefilmsproject.dto.UpdateAccessCodeRequest;
import com.photo.truefilmsproject.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/events")
@CrossOrigin(origins = "http://localhost:3000") // Permite la comunicación con tu frontend en Next.js
public class AdminEventController {

    private final EventService eventService;

    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    // GET /api/admin/events -> Listar todos los eventos (vista de administrador)
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    // POST /api/admin/events -> Crear un nuevo evento (con código automático o personalizado)
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        EventResponse createdEvent = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    // PATCH /api/admin/events/{id}/code -> Actualizar únicamente el código de acceso de un evento existente
    @PatchMapping("/{id}/code")
    public ResponseEntity<EventResponse> updateAccessCode(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAccessCodeRequest request) {
        EventResponse updatedEvent = eventService.updateAccessCode(id, request.newAccessCode());
        return ResponseEntity.ok(updatedEvent);
    }

    // DELETE /api/admin/events/{id} -> Eliminar un evento por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}