package net.czming.detection.review;

import net.czming.detection.review.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@SpringBootTest
public class MinioFileStorageServiceTest {

    @Autowired
    private FileStorageService minioFileStorageService;

    @Test
    public void uploadLocalImage() throws Exception {
        Path path = Path.of("C:\\Users\\likeai\\Desktop\\OIP.jpg");

        try (InputStream inputStream = Files.newInputStream(path)) {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    path.getFileName().toString(),
                    "image/jpeg",
                    inputStream
            );

            String objectKey = minioFileStorageService.uploadDetectedImage(file, LocalDateTime.now());
            System.out.println("上传成功，objectKey = " + objectKey);

            System.out.println(minioFileStorageService.getTemporaryUrl(objectKey));
        }
    }

    @Test
    public void deleteLocalImage() throws Exception {
        minioFileStorageService.deleteObject("2026-03-31/abcb7b74-3484-4664-a185-27ff76f0b9ee.jpg");
    }
}