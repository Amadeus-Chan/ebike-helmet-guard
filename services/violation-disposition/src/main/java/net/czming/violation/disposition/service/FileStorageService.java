package net.czming.violation.disposition.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.violation.disposition.properties.MinioProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class FileStorageService {

    public static final int URL_EXPIRATION_SECONDS = 60 * 60;

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;


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


}
