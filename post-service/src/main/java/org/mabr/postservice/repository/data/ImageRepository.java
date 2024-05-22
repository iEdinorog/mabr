package org.mabr.postservice.repository.data;

import org.mabr.postservice.entity.data.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    Optional<Image> findByCode(String name);
}
