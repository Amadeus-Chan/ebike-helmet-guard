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
public class ReviewTask {
    private Long id;
    private Long cameraId;
    private String detectedImage;
    private String location;
    private LocalDateTime captureTime;
    private LocalDateTime reviewTime;
    private boolean violated;
    private Status status;
    private Integer dispatcherCount;

    public enum Status {
        PENDING,
        PROCESSING,
        PROCESSED,
        INVALID
    }
}
