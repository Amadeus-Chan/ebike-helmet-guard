package net.czming.model.violation.disposition.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import net.czming.common.util.constants.ValidationConstants;
import net.czming.model.violation.disposition.entity.Appeal;
import net.czming.model.violation.disposition.entity.Violation;

import java.time.LocalDateTime;

@Data
public class AppealVo {
    private Long id;

    private Long violationId;

    @JsonFormat(pattern = ValidationConstants.DATETIME_PATTERN)
    private LocalDateTime createTime;

    private String auditorUsername;

    private String status;

    @JsonFormat(pattern = ValidationConstants.DATETIME_PATTERN)
    private LocalDateTime processTime;

    private ViolationVo violation;
}
