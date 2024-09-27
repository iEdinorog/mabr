package org.mabr.filestorageservice.service.image;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    private final ImageStorageService storageService;
    private final AsyncImageService asyncImageService;

    public List<String> upload(List<MultipartFile> files) {
        storageService.createBucket();

        var futures = new ArrayList<CompletableFuture<String>>();
        for (var file : files) {
            var future = asyncImageService.save(file);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    @SneakyThrows
    public byte[] getImage(String imageName) {
        byte[] data = new byte[1024];

        var inputStream = storageService.getInputStream(imageName);

        var buffer = new ByteArrayOutputStream();
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    public void deleteImage(String imageName) {
        storageService.deleteImage(imageName);
    }
}
