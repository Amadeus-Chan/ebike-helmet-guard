package net.czming.model.detection.review.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRecord {
    private Long id;
    private Long cameraId;
    private String detectedImage;
    private boolean violated;
    private LocalDateTime reviewTime;
    private LocalDateTime captureTime;
}
