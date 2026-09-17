package com.photo.truefilmsproject.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private LocalDate eventDate;

    private String zipFileName;

    private LocalDateTime createdAt;

    // getters y setters

    public Event() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public String getZipFileName() { return zipFileName; }
    public void setZipFileName(String zipFileName) { this.zipFileName = zipFileName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}