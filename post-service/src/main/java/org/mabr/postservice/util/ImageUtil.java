package org.mabr.postservice.util;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtil {

    private static final int MIN_LARGE_FILE_SIZE = 1000;
    private static final int MIN_IMAGE_SIZE = 150000;

    public static byte[] compress(MultipartFile inputFile) throws IOException {
        var inputFileSize = inputFile.getSize();
        var originalImage = ImageIO.read(inputFile.getInputStream());
        var outputStream = new ByteArrayOutputStream();
        var quality = 0.6;

        if (inputFileSize >= MIN_LARGE_FILE_SIZE) {
            quality = 0.2;
        }

        if (inputFileSize <= MIN_IMAGE_SIZE) {
            return inputFile.getBytes();
        }

        Thumbnails.of(originalImage)
                .scale(1.0)
                .outputFormat("JPEG")
                .outputQuality(quality)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }
}
