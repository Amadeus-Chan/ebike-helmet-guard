package net.czming.detection.review.config.initializer;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.czming.common.exception.BusinessException;
import net.czming.common.exception.ErrorEnum;
import net.czming.detection.review.properties.MinioProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioBucketInitializer {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;


    @PostConstruct
    public void init() {
        try {
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .build();

            boolean existed = minioClient.bucketExists(bucketExistsArgs);

            if (!existed) {
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(minioProperties.getBucket()).build();
                minioClient.makeBucket(makeBucketArgs);
            }

        } catch (Exception e) {
            throw new BusinessException(ErrorEnum.SYSTEM_INIT_FAILED, "初始化 MinIO bucket 失败", e);
        }
    }
}
