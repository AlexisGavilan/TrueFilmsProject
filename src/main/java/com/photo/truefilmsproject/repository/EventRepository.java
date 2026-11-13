package com.photo.truefilmsproject.repository;

import com.photo.truefilmsproject.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    //Search the event using the private code
    Optional<Event> findByAccessCode(String accessCode);

    // NUEVO: Buscar eventos caducados antes de la fecha actual (HU-4.2)
    List<Event> findByExpiresAtBefore(LocalDateTime dateTime);
}

