package net.czming.detection.review.mapper;

import net.czming.model.detection.review.entity.ReviewTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewTaskMapper {

    int insertReviewTask(ReviewTask reviewTask);

    ReviewTask selectReviewTaskById(Long id);

    List<ReviewTask> selectPendingReviewTask();

    int updateReviewTask(ReviewTask reviewTask);

    int markProcessingIfPending(Long id);
}
