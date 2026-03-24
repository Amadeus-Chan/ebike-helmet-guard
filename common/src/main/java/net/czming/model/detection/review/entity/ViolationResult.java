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
public class ViolationResult {
    private Long id;
    private Long recordId;
    private String licensePlate;
    private String evidenceImage;
    private String location;
    private LocalDateTime violationTime;
}
