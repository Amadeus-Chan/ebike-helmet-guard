package net.czming.model.violation.disposition.entity;

import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import lombok.Data;
import net.czming.model.violation.disposition.dto.EbikeAddDto;
import net.czming.model.violation.disposition.dto.EbikeUpdateDto;
import net.czming.model.violation.disposition.vo.EbikeVo;

@Data
@AutoMappers({
        @AutoMapper(target = EbikeVo.class),
        @AutoMapper(target = EbikeUpdateDto.class),
        @AutoMapper(target = EbikeAddDto.class)
})
public class Ebike {
    private Long id;
    private String licensePlate;
    private String featureDescription;
    private String ownerName;
    private String ownerIdCard;
    private String ownerPhoneNumber;
}
