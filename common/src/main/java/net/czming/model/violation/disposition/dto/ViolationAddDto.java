package net.czming.model.violation.disposition.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.czming.common.util.constants.ValidationConstants;

import java.time.LocalDateTime;


@Data
public class ViolationAddDto {
    @NotBlank(message = "licensePlate不能为空")
    private String licensePlate;

    @NotBlank(message = "location不能为空")
    private String location;

    @NotBlank(message = "evidenceImage不能为空")
    private String evidenceImage;

    @NotNull(message = "time不能为空")
    @JsonFormat(pattern = ValidationConstants.DATETIME_PATTERN)
    private LocalDateTime time;
}
