package net.czming.violation.disposition.mapper;

import net.czming.model.violation.disposition.entity.Violation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ViolationMapper {

    Violation selectViolationById(Long id);

    List<Violation> selectViolationsByLicensePlate( String licensePlate);

    int updateViolation( Violation violation);

    int insertViolation( Violation violation);

    int batchInsertViolation( List<Violation> violationList);
}
