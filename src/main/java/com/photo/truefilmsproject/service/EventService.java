package com.photo.truefilmsproject.service;

import com.photo.truefilmsproject.dto.EventRequest;
import com.photo.truefilmsproject.dto.EventResponse;
import com.photo.truefilmsproject.entity.Event;
import com.photo.truefilmsproject.exception.EventNotFoundException;
import com.photo.truefilmsproject.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

        Event savedEvent = eventRepository.save(event);
        return mapToResponse(savedEvent);
    }

    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException(id);
        }
        eventRepository.deleteById(id);
    }

    private EventResponse mapToResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getEventDate(),
                event.getZipFileName() != null
        );
    }
}