package com.photo.truefilmsproject.repository;

import com.photo.truefilmsproject.entity.AboutMeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AboutMeProfileRepository extends JpaRepository<AboutMeProfile, Long> {

}