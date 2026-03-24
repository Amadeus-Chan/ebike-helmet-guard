package net.czming.detection.review.mapper;

import net.czming.model.detection.review.entity.ViolationResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ViolationResultMapper {
    int batchInsertViolationResult(List<ViolationResult> violationResultList);
}
