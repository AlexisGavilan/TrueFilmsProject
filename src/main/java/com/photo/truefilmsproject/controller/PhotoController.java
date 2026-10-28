package com.photo.truefilmsproject.controller;

import com.photo.truefilmsproject.dto.PhotoResponse;
import com.photo.truefilmsproject.dto.DownloadSelectionRequest;
import com.photo.truefilmsproject.service.PhotoService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.io.IOException;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    // Obtener la lista de fotos de un evento
    @GetMapping("/events/{eventId}/photos")
    public ResponseEntity<List<PhotoResponse>> getEventPhotos(@PathVariable Long eventId) {
        return ResponseEntity.ok(photoService.getPhotosByEvent(eventId));
    }

    // Obtener la miniatura (thumbnail) optimizada
    @GetMapping("/photos/{id}/thumbnail")
    public ResponseEntity<Resource> getThumbnail(@PathVariable Long id) {
        Resource resource = photoService.loadPhotoAsResource(id, true);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    // Obtener la foto original / alta resolución
    @GetMapping("/photos/{id}/raw")
    public ResponseEntity<Resource> getRawPhoto(@PathVariable Long id) {
        Resource resource = photoService.loadPhotoAsResource(id, false);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @PostMapping("/photos/download-selection")
    public ResponseEntity<byte[]> downloadSelectedPhotos(@Valid @RequestBody DownloadSelectionRequest request) {
        try {
            byte[] zipData = photoService.createZipFromSelectedPhotos(request.photoIds());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "fotos-seleccionadas.zip");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipData);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}