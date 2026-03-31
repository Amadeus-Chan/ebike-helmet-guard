package net.czming.detection.review.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.detection.review.properties.MinioProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileStorageService {

    public static final int URL_EXPIRATION_SECONDS = 60 * 60;

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String uploadDetectedImage(MultipartFile detectedImageFile, LocalDateTime captureTime) {
        String datePath = captureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String contentType = detectedImageFile.getContentType();
        String objectKey = datePath + "/" + UUID.randomUUID() + resolveExtension(contentType);

        try (InputStream inputStream = detectedImageFile.getInputStream()){
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectKey)
                            .stream(inputStream, detectedImageFile.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );

        }catch (Exception e){
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "上传到minio失败", e);
        }

        return objectKey;
    }

    public String getTemporaryUrl(String objectKey) {

        if (!StringUtils.hasText(objectKey))
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "objectKey不能为空");


        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioProperties.getBucket())
                            .object(objectKey)
                            .expiry(URL_EXPIRATION_SECONDS)
                            .build()
            );
        } catch (Exception e) {
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "获取图片URL失败", e);
        }
    }

    public void deleteObject(String objectKey) {
        if (!StringUtils.hasText(objectKey))
            return;

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectKey)
                            .build()
            );
        }catch (Exception e){
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "删除minio对象失败", e);
        }
    }

    public GetObjectResponse getObject(String objectKey) {
        if (!StringUtils.hasText(objectKey))
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "objectKey不能为空");

        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectKey)
                            .build()
            );
        }catch (Exception e){
            throw new BusinessException(ErrorEnum.BIZ_FAILED, "读取minio对象失败", e);
        }
    }

    private String resolveExtension(String contentType) {

        if (MediaType.IMAGE_JPEG_VALUE.equals(contentType)) {
            return ".jpg";
        }
        else if (MediaType.IMAGE_PNG_VALUE.equals(contentType)) {
            return ".png";
        }

        throw new BusinessException(ErrorEnum.BIZ_FAILED, "contentType必须为jpg或png");
    }

}
