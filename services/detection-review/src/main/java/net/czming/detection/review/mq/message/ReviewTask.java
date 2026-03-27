package net.czming.detection.review.mq.message;

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
    private Long cameraId;
    private String detectedImageUrl;
    private String location;
    private LocalDateTime captureTime;
}
