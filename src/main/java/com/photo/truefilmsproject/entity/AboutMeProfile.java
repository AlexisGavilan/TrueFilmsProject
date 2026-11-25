package com.photo.truefilmsproject.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "about_me_profile")
public class AboutMeProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String heroImageUrl = "";

    @Column(nullable = false)
    private String title = "Quien soy?";

    @Column(length = 500)
    private String subtitle = "";

    @Column(columnDefinition = "TEXT")
    private String description = "";

    @Column(nullable = false)
    private String galleryImage1Url = "";

    @Column(nullable = false)
    private String galleryImage2Url = "";

    @Column(nullable = false)
    private String galleryImage3Url = "";

    @Column(nullable = false)
    private String galleryImage4Url = "";

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onSaveOrUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public AboutMeProfile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getHeroImageUrl() { return heroImageUrl; }
    public void setHeroImageUrl(String heroImageUrl) { this.heroImageUrl = heroImageUrl; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGalleryImage1Url() { return galleryImage1Url; }
    public void setGalleryImage1Url(String galleryImage1Url) { this.galleryImage1Url = galleryImage1Url; }

    public String getGalleryImage2Url() { return galleryImage2Url; }
    public void setGalleryImage2Url(String galleryImage2Url) { this.galleryImage2Url = galleryImage2Url; }

    public String getGalleryImage3Url() { return galleryImage3Url; }
    public void setGalleryImage3Url(String galleryImage3Url) { this.galleryImage3Url = galleryImage3Url; }

    public String getGalleryImage4Url() { return galleryImage4Url; }
    public void setGalleryImage4Url(String galleryImage4Url) { this.galleryImage4Url = galleryImage4Url; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}