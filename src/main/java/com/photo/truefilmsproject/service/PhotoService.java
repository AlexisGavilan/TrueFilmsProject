package com.photo.truefilmsproject.service;

import com.photo.truefilmsproject.dto.PhotoResponse;
import com.photo.truefilmsproject.entity.Event;
import com.photo.truefilmsproject.entity.Photo;
import com.photo.truefilmsproject.exception.ResourceNotFoundException;
import com.photo.truefilmsproject.repository.EventRepository;
import com.photo.truefilmsproject.repository.PhotoRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final EventRepository eventRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public PhotoService(PhotoRepository photoRepository, EventRepository eventRepository) {
        this.photoRepository = photoRepository;
        this.eventRepository = eventRepository;
    }

    public List<PhotoResponse> getPhotosByEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Evento no encontrado");
        }
        return photoRepository.findByEventId(eventId).stream()
                .map(photo -> new PhotoResponse(
                        photo.getId(),
                        photo.getFileName(),
                        "/api/photos/" + photo.getId() + "/thumbnail",
                        "/api/photos/" + photo.getId() + "/raw",
                        photo.getFileSize()
                ))
                .toList();
    }

    public Resource loadPhotoAsResource(Long photoId, boolean isThumbnail) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Fotografía no encontrada"));

        String pathStr = isThumbnail && photo.getThumbnailPath() != null
                ? photo.getThumbnailPath()
                : photo.getFilePath();

        try {
            Path path = Paths.get(pathStr);
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("No se puede leer la imagen");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error en la ruta del archivo", e);
        }
    }
}
