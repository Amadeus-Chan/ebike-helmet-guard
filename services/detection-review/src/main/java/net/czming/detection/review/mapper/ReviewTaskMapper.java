package net.czming.detection.review.mapper;

import net.czming.model.detection.review.entity.ReviewTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReviewTaskMapper {

    int insertReviewTask(ReviewTask reviewTask);

    ReviewTask selectReviewTaskById(Long id);

    int updateReviewTask(ReviewTask reviewTask);
}
