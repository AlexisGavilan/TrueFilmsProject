package com.photo.truefilmsproject.service;

import com.photo.truefilmsproject.dto.CreateFilmItemRequest;
import com.photo.truefilmsproject.dto.FilmItemResponse;
import com.photo.truefilmsproject.entity.FilmItem;
import com.photo.truefilmsproject.exception.ResourceNotFoundException;
import com.photo.truefilmsproject.repository.FilmItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilmService {

    private final FilmItemRepository repository;

    public FilmService(FilmItemRepository repository) {
        this.repository = repository;
    }

    public List<FilmItemResponse> getAllFilms() {
        return repository.findAllByOrderByTitleDesc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public FilmItemResponse getFilmById(Long id) {
        FilmItem film = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cortometraje no encontrado con ID: " + id));
        return mapToResponse(film);
    }

    public FilmItemResponse createFilm(CreateFilmItemRequest request) {
        FilmItem film = new FilmItem();
        film.setTitle(request.title());
        film.setSynopsis(request.synopsis());
        film.setVideoUrl(request.videoUrl());
        film.setThumbnailUrl(request.thumbnailUrl());
        film.setReleaseYear(request.releaseYear());
        film.setDurationMinutes(request.durationMinutes());

        FilmItem saved = repository.save(film);
        return mapToResponse(saved);
    }

    public void deleteFilm(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cortometraje no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    private FilmItemResponse mapToResponse(FilmItem film) {
        return new FilmItemResponse(
                film.getId(),
                film.getTitle(),
                film.getSynopsis(),
                film.getVideoUrl(),
                film.getThumbnailUrl(),
                film.getReleaseYear(),
                film.getDurationMinutes()
        );
    }
}