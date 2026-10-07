package com.photo.truefilmsproject.controller;

import com.photo.truefilmsproject.dto.CreateFilmItemRequest;
import com.photo.truefilmsproject.dto.FilmItemResponse;
import com.photo.truefilmsproject.service.FilmService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/films")
@CrossOrigin(origins = "http://localhost:3000")
public class FilmController {

    private final FilmService service;

    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<FilmItemResponse>> getAllFilms() {
        return ResponseEntity.ok(service.getAllFilms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmItemResponse> getFilmById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getFilmById(id));
    }

    @PostMapping
    public ResponseEntity<FilmItemResponse> createFilm(@Valid @RequestBody CreateFilmItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createFilm(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable Long id) {
        service.deleteFilm(id);
        return ResponseEntity.noContent().build();
    }
}