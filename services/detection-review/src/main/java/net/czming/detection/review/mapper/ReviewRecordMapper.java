package net.czming.detection.review.mapper;

import net.czming.model.detection.review.entity.ReviewRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReviewRecordMapper {

    int insertReviewRecord(ReviewRecord reviewRecord);

    int updateViolated(Long id, Boolean violated);
}
