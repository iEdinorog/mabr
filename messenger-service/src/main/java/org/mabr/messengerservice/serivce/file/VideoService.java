package org.mabr.messengerservice.serivce.file;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mabr.messengerservice.entity.VideoFileMetadata;
import org.mabr.messengerservice.repository.VideoFileMetadataRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoStorageService videoStorageService;
    private final VideoFileMetadataRepository videoFileMetadataRepository;

    @Transactional
    public String save(MultipartFile file) {
        var fileUuid = UUID.randomUUID().toString();
        var metadata = VideoFileMetadata.builder()
                .id(fileUuid)
                .size(file.getSize())
                .httpContentType(file.getContentType())
                .build();

        videoFileMetadataRepository.save(metadata);
        videoStorageService.save(file, fileUuid);

        return createVideoUrl(fileUuid);
    }

    public ChunkWithMetadata fetchChunk(String uuid, Range range) {
        var videoFileMetadata = videoFileMetadataRepository.findById(uuid).orElseThrow();
        return new ChunkWithMetadata(videoFileMetadata, readChunk(uuid, range, videoFileMetadata.getSize()));

    }

    private byte[] readChunk(String uuid, Range range, long fileSize) {
        var startPosition = range.getRangeStart();
        var endPosition = range.getRangeEnd(fileSize);
        var chunkSize = (int) (endPosition - startPosition + 1);
        try (InputStream inputStream = videoStorageService.getInputStream(uuid, startPosition, chunkSize)) {
            return inputStream.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createVideoUrl(String uuid) {
        return "http://localhost:8080/messenger/api/video/" + uuid;
    }

    @Transactional
    public void deleteVideo(String uuid) {
        var metadata = videoFileMetadataRepository.findById(uuid).orElseThrow();

        videoFileMetadataRepository.delete(metadata);
        videoStorageService.delete(metadata.getId());
    }

    public record ChunkWithMetadata(
            VideoFileMetadata metadata,
            byte[] chunk
    ) {
    }
}
