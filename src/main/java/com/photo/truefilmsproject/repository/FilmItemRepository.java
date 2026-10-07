package com.photo.truefilmsproject.repository;

import com.photo.truefilmsproject.entity.FilmItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmItemRepository extends JpaRepository<FilmItem, Long> {
    List<FilmItem> findAllByOrderByReleaseYearDesc();
    List<FilmItem> findAllByOrderByTitleAsc();
    List<FilmItem> findAllByOrderByTitleDesc();
}