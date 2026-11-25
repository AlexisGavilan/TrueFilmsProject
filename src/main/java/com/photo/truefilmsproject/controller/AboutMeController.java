package com.photo.truefilmsproject.controller;

import com.photo.truefilmsproject.dto.AboutMeResponse;
import com.photo.truefilmsproject.service.AboutMeService;
import com.photo.truefilmsproject.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/about-me")
@CrossOrigin(origins = "http://localhost:3000")
public class AboutMeController {

    private final AboutMeService aboutMeService;
    private final FileStorageService fileStorageService;

    public AboutMeController(AboutMeService aboutMeService, FileStorageService fileStorageService) {
        this.aboutMeService = aboutMeService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public ResponseEntity<AboutMeResponse> getAboutMe() {
        return ResponseEntity.ok(aboutMeService.getOrCreateProfile());
    }

    @GetMapping("/images/{slot}")
    public ResponseEntity<Resource> getSlotImage(@PathVariable String slot) {
        Resource resource = fileStorageService.loadAboutMeImage(slot);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        String contentType = "image/jpeg";
        try {
            String detected = Files.probeContentType(resource.getFile().toPath());
            if (detected != null) {
                contentType = detected;
            }
        } catch (IOException ignored) {
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}