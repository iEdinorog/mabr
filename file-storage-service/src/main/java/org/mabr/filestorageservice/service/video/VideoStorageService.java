package org.mabr.filestorageservice.service.video;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.mabr.filestorageservice.props.MinioProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class VideoStorageService {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public void save(MultipartFile file, String fileName) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), properties.getPutObjectPartSize())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to save video file " + e.getMessage());
        }
    }

    public InputStream getInputStream(String fileName, long offset, long length) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .offset(offset)
                    .length(length)
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get inputStream for file " + fileName);
        }
    }

    public void delete(String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(fileName)
                    .build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | InternalException |
                 InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
    }
}
