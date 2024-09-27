package org.mabr.filestorageservice.service.image;

import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mabr.filestorageservice.exception.ImageUploadException;
import org.mabr.filestorageservice.props.MinioProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageStorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public void createBucket() {
        try {
            var found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .build());

            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .build());
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new ImageUploadException("Image upload failed " + e.getMessage());
        }
    }

    @SneakyThrows
    public void saveImage(InputStream inputStream, String fileName) {
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

    public InputStream getInputStream(String imageName) {
        try {
            return minioClient.getObject(GetObjectArgs
                    .builder()
                    .bucket(minioProperties.getBucket())
                    .object(imageName)
                    .build());
        } catch (Exception e) {
            throw new ImageUploadException(e.getMessage());
        }
    }
}
