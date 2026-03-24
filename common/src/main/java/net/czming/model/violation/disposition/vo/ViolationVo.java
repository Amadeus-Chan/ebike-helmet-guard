package net.czming.model.violation.disposition.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import net.czming.common.util.constants.ValidationConstants;

import java.time.LocalDateTime;

@Data
public class ViolationVo {
    private Long id;

    private String licensePlate;

    private String location;

    private String evidenceImage;

    @JsonFormat(pattern = ValidationConstants.DATETIME_PATTERN)
    private LocalDateTime time;

    private String status;
}
