package com.photo.truefilmsproject.service;

import com.photo.truefilmsproject.entity.Event;
import com.photo.truefilmsproject.repository.EventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CleanupService {

    private final EventRepository eventRepository;
    private final EventService eventService;

    public CleanupService(EventRepository eventRepository, EventService eventService) {
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    /**
     * Se ejecuta automáticamente de forma periódica.
     * Por defecto, configurado para ejecutarse todos los días a las 03:00 AM.
     * (Puedes cambiar el cron o usar fixedRate si prefieres pruebas más rápidas).
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupExpiredEvents() {
        LocalDateTime now = LocalDateTime.now();

        // Buscamos todos los eventos cuya fecha de expiración sea anterior a la actual
        List<Event> expiredEvents = eventRepository.findByExpiresAtBefore(now);

        if (expiredEvents.isEmpty()) {
            return;
        }

        System.out.println("🧹 [CleanupService] Encontrados " + expiredEvents.size() + " eventos caducados. Procediendo a borrarlos...");

        for (Event event : expiredEvents) {
            try {
                // Llamamos a eventService.deleteEvent para que borre:
                // 1. Los archivos físicos del disco (ZIP, originals, thumbnails) mediante FileStorageService
                // 2. El registro en la base de datos
                eventService.deleteEvent(event.getId());
                System.out.println("✅ Evento caducado eliminado con éxito: ID " + event.getId() + " (" + event.getTitle() + ")");
            } catch (Exception e) {
                System.err.println("❌ Error al eliminar el evento caducado ID " + event.getId() + ": " + e.getMessage());
            }
        }
    }
}