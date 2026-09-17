package com.photo.truefilmsproject.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(Long id) {
        super("Evento no encontrado con ID: " + id);
    }
}