package com.photo.truefilmsproject.service;

import com.photo.truefilmsproject.dto.AboutMeRequest;
import com.photo.truefilmsproject.dto.AboutMeResponse;
import com.photo.truefilmsproject.entity.AboutMeProfile;
import com.photo.truefilmsproject.repository.AboutMeProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
public class AboutMeService {

    private final AboutMeProfileRepository repository;
    private final FileStorageService fileStorageService;

    public AboutMeService(AboutMeProfileRepository repository, FileStorageService fileStorageService) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public AboutMeResponse getOrCreateProfile() {
        AboutMeProfile profile = repository.findAll().stream()
                .findFirst()
                .orElseGet(() -> repository.save(new AboutMeProfile()));
        return mapToResponse(profile);
    }

    @Transactional
    public AboutMeResponse updateProfile(AboutMeRequest request) {
        AboutMeProfile profile = repository.findAll().stream()
                .findFirst()
                .orElseGet(AboutMeProfile::new);

        if (request.heroImageUrl() != null) {
            profile.setHeroImageUrl(request.heroImageUrl());
        }
        profile.setTitle(request.title());
        profile.setSubtitle(request.subtitle() != null ? request.subtitle() : "");
        profile.setDescription(request.description() != null ? request.description() : "");

        if (request.galleryImages() != null && request.galleryImages().size() == 4) {
            profile.setGalleryImage1Url(request.galleryImages().get(0) != null ? request.galleryImages().get(0) : "");
            profile.setGalleryImage2Url(request.galleryImages().get(1) != null ? request.galleryImages().get(1) : "");
            profile.setGalleryImage3Url(request.galleryImages().get(2) != null ? request.galleryImages().get(2) : "");
            profile.setGalleryImage4Url(request.galleryImages().get(3) != null ? request.galleryImages().get(3) : "");
        }

        AboutMeProfile saved = repository.save(profile);
        return mapToResponse(saved);
    }

    @Transactional
    public AboutMeResponse uploadSlotImage(String slot, MultipartFile file) {
        String imageUrl = fileStorageService.storeAboutMeImage(slot, file);
        AboutMeProfile profile = repository.findAll().stream()
                .findFirst()
                .orElseGet(AboutMeProfile::new);

        switch (slot) {
            case "hero" -> profile.setHeroImageUrl(imageUrl);
            case "gallery-1" -> profile.setGalleryImage1Url(imageUrl);
            case "gallery-2" -> profile.setGalleryImage2Url(imageUrl);
            case "gallery-3" -> profile.setGalleryImage3Url(imageUrl);
            case "gallery-4" -> profile.setGalleryImage4Url(imageUrl);
            default -> throw new IllegalArgumentException("Slot no válido: " + slot);
        }

        AboutMeProfile saved = repository.save(profile);
        return mapToResponse(saved);
    }

    private AboutMeResponse mapToResponse(AboutMeProfile profile) {
        List<String> gallery = Arrays.asList(
                profile.getGalleryImage1Url(),
                profile.getGalleryImage2Url(),
                profile.getGalleryImage3Url(),
                profile.getGalleryImage4Url()
        );
        return new AboutMeResponse(
                profile.getId(),
                profile.getHeroImageUrl(),
                profile.getTitle(),
                profile.getSubtitle(),
                profile.getDescription(),
                gallery
        );
    }
}