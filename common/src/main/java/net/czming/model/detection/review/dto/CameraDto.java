package net.czming.model.detection.review.dto;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import net.czming.model.violation.disposition.vo.CameraVo;

@Data
@AutoMapper(target = CameraVo.class)
public class CameraDto {
    private Long id;

    private String location;
}
