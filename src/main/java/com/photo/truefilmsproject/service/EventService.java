package com.photo.truefilmsproject.service;

import com.photo.truefilmsproject.dto.EventRequest;
import com.photo.truefilmsproject.dto.EventResponse;
import com.photo.truefilmsproject.entity.Event;
import com.photo.truefilmsproject.exception.EventNotFoundException;
import com.photo.truefilmsproject.exception.ResourceNotFoundException;
import com.photo.truefilmsproject.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        return mapToResponse(event);
    }

    public EventResponse createEvent(EventRequest request) {
        Event event = new Event();
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setEventDate(request.eventDate());

        // Genera un código automático (UUID simplificado) si no viene especificado en la solicitud
        String code = (request.accessCode() != null && !request.accessCode().isBlank())
                ? request.accessCode()
                : UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        event.setAccessCode(code);

        Event savedEvent = eventRepository.save(event);
        return mapToResponse(savedEvent);
    }

    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException(id);
        }
        eventRepository.deleteById(id);
    }

    // HU-2.1: Obtener la entidad directamente usando el código de acceso privado
    public Event getEventByAccessCode(String code) {
        return eventRepository.findByAccessCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("No existe ningún evento con el código introducido: " + code));
    }

    private EventResponse mapToResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getEventDate(),
                event.getAccessCode(), // Incluimos el código de acceso en el DTO
                event.getZipFileName() != null
        );
    }
}