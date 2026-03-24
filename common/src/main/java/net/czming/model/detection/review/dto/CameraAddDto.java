package net.czming.model.detection.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import net.czming.common.util.constants.ValidationConstants;

@Data
public class CameraAddDto {

    @NotBlank(message = "location不能为空")
    private String location;

    @NotNull(message = "uuid不能为空")
    @Pattern(regexp = ValidationConstants.REGEX_CAMERA_SECRETE_KEY, message = "secreteKey格式错误")
    private String secretKey;
}
