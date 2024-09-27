package org.mabr.filestorageservice.service.image;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mabr.filestorageservice.exception.ImageUploadException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Slf4j
public class AsyncImageService {

    private final ImageProcessingService processingService;
    private final ImageStorageService storageService;

    @Async
    public CompletableFuture<String> save(MultipartFile file) {
        log.info(Thread.currentThread().getName());
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new ImageUploadException("Image upload failed");
        }

        var fileName = processingService.generateFileName(file);

        var inputStream = processingService.compressImage(file);

        storageService.saveImage(inputStream, fileName);

        var link = processingService.createImageLink(fileName);

        return CompletableFuture.completedFuture(link);
    }

}
