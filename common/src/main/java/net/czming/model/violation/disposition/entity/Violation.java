package net.czming.model.violation.disposition.entity;

import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.czming.model.violation.disposition.dto.ViolationAddDto;
import net.czming.model.violation.disposition.vo.ViolationVo;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@AutoMappers({
        @AutoMapper(target = ViolationAddDto.class),
        @AutoMapper(target = ViolationVo.class)
})
public class Violation {
    private Long id;
    private String licensePlate;
    private String location;
    private String evidenceImage;
    private LocalDateTime time;
    private Status status;

    public enum Status{
        UNAPPEALED,
        APPEALING,
        APPEAL_APPROVED,
        APPEAL_REJECTED,
    }
}
