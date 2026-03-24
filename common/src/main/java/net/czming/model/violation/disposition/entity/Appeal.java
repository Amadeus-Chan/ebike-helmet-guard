package net.czming.model.violation.disposition.entity;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.czming.model.violation.disposition.vo.AppealVo;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@AutoMapper(target = AppealVo.class)
public class Appeal {
    private Long id;
    private Long violationId;
    private LocalDateTime createTime;
    private String auditorUsername;
    private Status status;
    private LocalDateTime processTime;

    private Violation violation;

    public enum Status {
        PROCESSING,
        APPROVED,
        REJECTED,
    }
}
