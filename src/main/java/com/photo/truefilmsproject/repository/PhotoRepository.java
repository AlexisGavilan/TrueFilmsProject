package com.photo.truefilmsproject.repository;

import com.photo.truefilmsproject.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByEventId(Long eventId);
}
