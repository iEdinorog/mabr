package org.mabr.messengerservice.serivce;

import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import org.mabr.messengerservice.exception.ImageUploadException;
import org.mabr.messengerservice.props.MinioProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String upload(MultipartFile file) {
        try {
            createBucket();
        } catch (Exception e) {
            throw new ImageUploadException("Image upload failed " + e.getMessage());
        }

        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new ImageUploadException("Image upload failed");
        }

        var fileName = generateFileName(file);

        InputStream inputStream;

        inputStream = compressImage(file);

        saveImage(inputStream, fileName);

        return createImageLink(fileName);
    }

    @SneakyThrows
    private void createBucket() {
        var found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.getBucket())
                .build());

        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .build());
        }
    }

    private String generateFileName(MultipartFile file) {
        var extension = getExtension(file);
        return UUID.randomUUID() + "." + extension;
    }

    private String getExtension(MultipartFile file) {
        return file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
    }

    @SneakyThrows
    private void saveImage(InputStream inputStream, String fileName) {
        minioClient.putObject(PutObjectArgs.builder()
                .stream(inputStream, inputStream.available(), -1)
                .bucket(minioProperties.getBucket())
                .object(fileName)
                .build());
    }

    public void deleteImage(String imageName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(imageName)
                    .build());
        } catch (InvalidKeyException | NoSuchAlgorithmException | ErrorResponseException |
                 InsufficientDataException | InternalException | InvalidResponseException | IOException |
                 ServerException | XmlParserException e) {
            throw new ImageUploadException("Image deletion failed" + e.getMessage());
        }
    }

    public byte[] getImage(String imageName) {
        byte[] data = new byte[1024];
        try {
            var inputStream = minioClient.getObject(GetObjectArgs
                    .builder()
                    .bucket(minioProperties.getBucket())
                    .object(imageName)
                    .build());

            var buffer = new ByteArrayOutputStream();
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            return buffer.toByteArray();
        } catch (Exception e) {
            throw new ImageUploadException(e.getMessage());
        }
    }

    private String createImageLink(String imageName) {
        return "http://localhost:8080/messenger/api/image/" + imageName;
    }

    private InputStream compressImage(MultipartFile file) {
        var outputStream = new ByteArrayOutputStream();

        try {
            var bufferedImage = ImageIO.read(file.getInputStream());

            Thumbnails.of(bufferedImage)
                    .scale(1.0)
                    .outputQuality(0.1)
                    .outputFormat(getExtension(file))
                    .toOutputStream(outputStream);

            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
