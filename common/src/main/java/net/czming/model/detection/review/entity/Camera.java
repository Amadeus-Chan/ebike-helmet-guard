package net.czming.model.detection.review.entity;

import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import lombok.Data;
import net.czming.model.detection.review.dto.CameraAddDto;
import net.czming.model.detection.review.dto.CameraDto;

@Data
@AutoMappers({
        @AutoMapper(target = CameraAddDto.class),
        @AutoMapper(target = CameraDto.class)
})
public class Camera {
    private Long id;

    private String location;

    private String secretKey;
}
