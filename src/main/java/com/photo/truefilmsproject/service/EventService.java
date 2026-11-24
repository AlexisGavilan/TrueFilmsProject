package com.photo.truefilmsproject.service;

import com.photo.truefilmsproject.dto.EventRequest;
import com.photo.truefilmsproject.dto.EventResponse;
import com.photo.truefilmsproject.entity.Event;
import com.photo.truefilmsproject.exception.EventNotFoundException;
import com.photo.truefilmsproject.exception.ResourceNotFoundException;
import com.photo.truefilmsproject.repository.EventRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final FileStorageService fileStorageService;

    public EventService(EventRepository eventRepository, FileStorageService fileStorageService) {
        this.eventRepository = eventRepository;
        this.fileStorageService = fileStorageService;
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

        String code = (request.accessCode() != null && !request.accessCode().isBlank())
                ? request.accessCode()
                : UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        event.setAccessCode(code);

        int days = (request.expiresAt() != null && request.expiresAt() > 0)
                ? request.expiresAt()
                : 5;
        event.setExpiresAt(LocalDateTime.now().plusDays(days));

        Event savedEvent = eventRepository.save(event);
        return mapToResponse(savedEvent);
    }

    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException(id);
        }
        fileStorageService.deleteEventFiles(id);
        eventRepository.deleteById(id);
    }

    public Event getEventByAccessCode(String code) {
        Event event = eventRepository.findByAccessCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("No existe ningún evento con el código: " + code));

        if (LocalDateTime.now().isAfter(event.getExpiresAt())) {
            throw new IllegalStateException("El enlace de este evento ha caducado (límite de 5 días superado).");
        }

        return event;
    }

    public EventResponse uploadBannerImage(Long id, MultipartFile file) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        String bannerUrl = fileStorageService.storeBannerImage(id, file);
        event.setBannerImageUrl(bannerUrl);

        Event updatedEvent = eventRepository.save(event);
        return mapToResponse(updatedEvent);
    }

    public Resource getBannerResource(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException(id);
        }
        return fileStorageService.loadBannerFile(id);
    }

    private EventResponse mapToResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getEventDate(),
                event.getAccessCode(),
                event.getZipFileName() != null,
                event.getBannerImageUrl(),
                event.getCreatedAt(),
                event.getExpiresAt()
        );
    }

    public EventResponse updateAccessCode(Long id, String newAccessCode) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        String codeToUpdate = (newAccessCode != null && !newAccessCode.isBlank())
                ? newAccessCode
                : event.getAccessCode();

        event.setAccessCode(codeToUpdate);
        Event updatedEvent = eventRepository.save(event);

        return mapToResponse(updatedEvent);
    }
}