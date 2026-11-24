package com.photo.truefilmsproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.photo.truefilmsproject.dto.DiskSpaceResponse;
import com.photo.truefilmsproject.entity.Event;
import com.photo.truefilmsproject.entity.Photo;
import com.photo.truefilmsproject.repository.EventRepository;
import com.photo.truefilmsproject.repository.PhotoRepository;
import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FileStorageService {

    private final Path rootLocation;
    private final PhotoRepository photoRepository;
    private final EventRepository eventRepository;

    public FileStorageService(
            @Value("${file.upload-dir:./storage}") String uploadDir,
            PhotoRepository photoRepository,
            EventRepository eventRepository) {
        this.rootLocation = Paths.get(uploadDir);
        this.photoRepository = photoRepository;
        this.eventRepository = eventRepository;
        init();
    }

    public void init() {
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la carpeta de almacenamiento", e);
        }
    }

    public String storeBannerImage(Long eventId, MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("No se puede almacenar una imagen de banner vacía.");
            }

            String contentType = file.getContentType();
            if (contentType != null && !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("El archivo del banner debe ser una imagen válida.");
            }

            eventRepository.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + eventId));

            Path bannerDir = rootLocation.resolve("events/" + eventId + "/banner").normalize();
            Files.createDirectories(bannerDir);

            if (Files.exists(bannerDir)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(bannerDir)) {
                    for (Path existing : stream) {
                        Files.deleteIfExists(existing);
                    }
                }
            }

            String rawName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "banner.jpg";
            String simpleName = Paths.get(rawName).getFileName().toString().replaceAll("[^a-zA-Z0-9._-]", "_");

            Path destinationFile = bannerDir.resolve(simpleName).normalize();
            if (!destinationFile.startsWith(bannerDir)) {
                throw new SecurityException("Ruta de archivo no válida.");
            }

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            return "/api/events/" + eventId + "/banner";
        } catch (IOException e) {
            throw new RuntimeException("Fallo al almacenar la imagen del banner.", e);
        }
    }

    public Resource loadBannerFile(Long eventId) {
        try {
            Path bannerDir = rootLocation.resolve("events/" + eventId + "/banner").normalize();
            if (!Files.exists(bannerDir) || !Files.isDirectory(bannerDir)) {
                return null;
            }

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(bannerDir)) {
                for (Path filePath : stream) {
                    if (Files.isRegularFile(filePath)) {
                        Resource resource = new UrlResource(filePath.toUri());
                        if (resource.exists() && resource.isReadable()) {
                            return resource;
                        }
                    }
                }
            }
            return null;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error al cargar la imagen del banner para el evento: " + eventId, e);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el directorio del banner para el evento: " + eventId, e);
        }
    }

    public void storeZipFile(Long eventId, MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("No se puede almacenar un archivo vacío.");
            }

            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + eventId));

            Path eventDirectory = rootLocation.resolve("events/" + eventId);
            Path originalsDir = eventDirectory.resolve("originals");
            Path thumbsDir = eventDirectory.resolve("thumbnails");

            Files.createDirectories(originalsDir);
            Files.createDirectories(thumbsDir);

            Path destinationFile = eventDirectory.resolve("photos.zip");
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
                ZipEntry zipEntry;
                while ((zipEntry = zis.getNextEntry()) != null) {
                    String fileName = zipEntry.getName();
                    String lowerName = fileName.toLowerCase();

                    if (zipEntry.isDirectory() ||
                            (!lowerName.endsWith(".jpg") && !lowerName.endsWith(".jpeg") && !lowerName.endsWith(".png")) ||
                            fileName.contains("__MACOSX") || fileName.contains(".DS_Store")) {
                        zis.closeEntry();
                        continue;
                    }

                    Path simpleFileNamePath = Paths.get(fileName).getFileName();
                    if (simpleFileNamePath == null) {
                        zis.closeEntry();
                        continue;
                    }
                    String simpleFileName = simpleFileNamePath.toString();
                    if (simpleFileName.startsWith(".")) {
                        zis.closeEntry();
                        continue;
                    }

                    Path originalFilePath = originalsDir.resolve(simpleFileName);
                    Path thumbFilePath = thumbsDir.resolve(simpleFileName);

                    Files.copy(zis, originalFilePath, StandardCopyOption.REPLACE_EXISTING);

                    try {
                        Thumbnails.of(originalFilePath.toFile())
                                .size(400, 400)
                                .outputFormat("jpg")
                                .toFile(thumbFilePath.toFile());
                    } catch (Exception e) {
                        // Continúa si falla una miniatura puntual
                    }

                    long fileSize = Files.size(originalFilePath);
                    Photo photo = new Photo();
                    photo.setFileName(simpleFileName);
                    photo.setFilePath(originalFilePath.toString());
                    photo.setThumbnailPath(thumbFilePath.toString());
                    photo.setFileSize(fileSize);
                    photo.setEvent(event);

                    photoRepository.save(photo);

                    zis.closeEntry();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Fallo al almacenar y descomprimir el archivo ZIP.", e);
        }
    }

    public DiskSpaceResponse getDiskSpaceInfo() {
        File rootDir = rootLocation.toFile();
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }

        long totalSpace = rootDir.getTotalSpace();
        long freeSpace = rootDir.getFreeSpace();
        long usableSpace = rootDir.getUsableSpace();
        long usedSpace = totalSpace - freeSpace;

        double usagePercentage = totalSpace > 0 ? (double) usedSpace / totalSpace * 100 : 0.0;

        return new DiskSpaceResponse(
                totalSpace,
                freeSpace,
                usableSpace,
                usedSpace,
                Math.round(usagePercentage * 100.0) / 100.0
        );
    }

    public void deleteEventFiles(Long eventId) {
        try {
            Path eventDirectory = rootLocation.resolve("events/" + eventId);
            if (Files.exists(eventDirectory)) {
                Files.walk(eventDirectory)
                        .sorted(java.util.Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron eliminar los archivos físicos del evento: " + eventId, e);
        }
    }
}