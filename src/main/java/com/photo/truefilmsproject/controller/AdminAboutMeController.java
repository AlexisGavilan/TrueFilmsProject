package com.photo.truefilmsproject.controller;

import com.photo.truefilmsproject.dto.AboutMeRequest;
import com.photo.truefilmsproject.dto.AboutMeResponse;
import com.photo.truefilmsproject.service.AboutMeService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/about-me")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminAboutMeController {

    private final AboutMeService aboutMeService;

    public AdminAboutMeController(AboutMeService aboutMeService) {
        this.aboutMeService = aboutMeService;
    }

    @PutMapping
    public ResponseEntity<AboutMeResponse> updateAboutMe(@Valid @RequestBody AboutMeRequest request) {
        return ResponseEntity.ok(aboutMeService.updateProfile(request));
    }

    @PostMapping(value = "/images/{slot}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AboutMeResponse> uploadImage(
            @PathVariable String slot,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(aboutMeService.uploadSlotImage(slot, file));
    }
}