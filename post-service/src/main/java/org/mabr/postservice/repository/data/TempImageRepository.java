package org.mabr.postservice.repository.data;

import org.mabr.postservice.entity.data.TempImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TempImageRepository extends JpaRepository<TempImage, Integer> {
    Optional<TempImage> findByUrl(String url);
}
