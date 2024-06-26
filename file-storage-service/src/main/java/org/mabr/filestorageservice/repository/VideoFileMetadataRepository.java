package org.mabr.filestorageservice.repository;


import org.mabr.filestorageservice.entity.VideoFileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoFileMetadataRepository extends JpaRepository<VideoFileMetadata, String> {
}
