package org.mabr.messengerservice.repository;

import org.mabr.messengerservice.entity.VideoFileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoFileMetadataRepository extends JpaRepository<VideoFileMetadata, String> {
}
