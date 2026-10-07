package com.photo.truefilmsproject.service;

import com.photo.truefilmsproject.dto.CreatePortfolioItemRequest;
import com.photo.truefilmsproject.dto.PortfolioItemResponse;
import com.photo.truefilmsproject.entity.PortfolioItem;
import com.photo.truefilmsproject.exception.ResourceNotFoundException;
import com.photo.truefilmsproject.repository.PortfolioItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioItemRepository repository;

    public PortfolioService(PortfolioItemRepository repository) {
        this.repository = repository;
    }

    public List<PortfolioItemResponse> getAllItems() {
        return repository.findAllByOrderByDisplayOrderAsc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PortfolioItemResponse getItemById(Long id) {
        PortfolioItem item = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Elemento del portfolio no encontrado con ID: " + id));
        return mapToResponse(item);
    }

    public PortfolioItemResponse createItem(CreatePortfolioItemRequest request) {
        PortfolioItem item = new PortfolioItem(
                request.title(),
                request.description(),
                request.mediaUrl(),
                request.mediaType(),
                request.displayOrder()
        );
        PortfolioItem saved = repository.save(item);
        return mapToResponse(saved);
    }

    public void deleteItem(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Elemento no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    private PortfolioItemResponse mapToResponse(PortfolioItem item) {
        return new PortfolioItemResponse(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getMediaUrl(),
                item.getMediaType(),
                item.getDisplayOrder()
        );
    }
}