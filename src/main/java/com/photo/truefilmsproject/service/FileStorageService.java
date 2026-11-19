package com.photo.truefilmsproject.service;

import org.springframework.beans.factory.annotation.Value;
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

    /**
     * Guarda el ZIP, lo descomprime al vuelo, genera miniaturas y registra las fotos en BD.
     */
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

            // 1. Guardar el archivo ZIP completo en disco (para descargas masivas si procede)
            Path destinationFile = eventDirectory.resolve("photos.zip");
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // 2. Leer y descomprimir el ZIP para extraer imágenes y guardarlas individualmente
            try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
                ZipEntry zipEntry;
                while ((zipEntry = zis.getNextEntry()) != null) {
                    String fileName = zipEntry.getName();
                    String lowerName = fileName.toLowerCase();

                    // Filtrar directorios, archivos ocultos del sistema (__MACOSX, .DS_Store) y extensiones no válidas
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

                    // Copiar el archivo original de la foto al disco
                    Files.copy(zis, originalFilePath, StandardCopyOption.REPLACE_EXISTING);

                    // Generar la miniatura optimizada con Thumbnailator
                    try {
                        Thumbnails.of(originalFilePath.toFile())
                                .size(400, 400)
                                .outputFormat("jpg")
                                .toFile(thumbFilePath.toFile());
                    } catch (Exception e) {
                        // Si falla la miniatura de alguna imagen en concreto, continúa
                    }

                    // Guardar los metadatos y rutas en la base de datos vinculados al evento
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
                // Borra recursivamente la carpeta del evento (photos.zip, originals, thumbnails)
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