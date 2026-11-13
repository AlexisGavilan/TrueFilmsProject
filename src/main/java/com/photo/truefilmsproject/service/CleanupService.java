package com.photo.truefilmsproject.service;

import com.photo.truefilmsproject.entity.Event;
import com.photo.truefilmsproject.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CleanupService {

    private static final Logger log = LoggerFactory.getLogger(CleanupService.class);
    private final EventRepository eventRepository;

    public CleanupService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // Se ejecuta automáticamente todos los días a las 03:00 AM (HU-4.2)[cite: 3]
    @Scheduled(cron = "0 0 3 * * *")
    public void purgeExpiredEvents() {
        log.info("Ejecutando tarea programada de limpieza de eventos caducados...");

        List<Event> expiredEvents = eventRepository.findByExpiresAtBefore(LocalDateTime.now());

        for (Event event : expiredEvents) {
            try {
                // 1. Borrar carpeta física y archivos del disco
                Path eventFolder = Paths.get("storage/events/" + event.getId());
                if (Files.exists(eventFolder)) {
                    Files.walk(eventFolder)
                            .sorted((p1, p2) -> -p1.compareTo(p2))
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                } catch (IOException e) {
                                    log.error("Error al borrar archivo físico: " + path, e);
                                }
                            });
                }

                // 2. Borrar registro de la base de datos
                eventRepository.delete(event);
                log.info("Evento ID {} eliminado correctamente por caducidad.", event.getId());

            } catch (Exception e) {
                log.error("Error al limpiar el evento ID: " + event.getId(), e);
            }
        }
    }
}