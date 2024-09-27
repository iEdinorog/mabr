package org.mabr.filestorageservice.service.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageProcessingService {

    public String generateFileName(MultipartFile file) {
        var extension = getExtension(file);
        return UUID.randomUUID() + "." + extension;
    }

    private String getExtension(MultipartFile file) {
        return file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
    }


    public String createImageLink(String imageName) {
        return "http://localhost:8080/file-storage/api/image/" + imageName;
    }

    public InputStream compressImage(MultipartFile file) {
        var outputStream = new ByteArrayOutputStream();

        try {
            var bufferedImage = ImageIO.read(file.getInputStream());

            Thumbnails.of(bufferedImage)
                    .scale(1.0)
                    .outputQuality(0.4)
                    .outputFormat(getExtension(file))
                    .toOutputStream(outputStream);

            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
